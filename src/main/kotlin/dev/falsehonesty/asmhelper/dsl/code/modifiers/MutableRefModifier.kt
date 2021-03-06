package dev.falsehonesty.asmhelper.dsl.code.modifiers

import dev.falsehonesty.asmhelper.printing.logger
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class MutableRefModifier(val codeBlockMethodNode: MethodNode, val codeBlockClass: String) : Modifier() {
    override fun modify(instructions: InsnList) {
        var node = instructions.first
        while (node.next != null) {
            node = node.next

            if (node is FieldInsnNode && node.owner == codeBlockClass && node.desc.startsWith("Lkotlin/jvm/internal/Ref$")) {
                var next = node.next
                var allowedActions = 1

                while (next.opcode == Opcodes.DUP) {
                    instructions.remove(next)
                    instructions.insertBefore(node, next)
                    next = node.next
                    allowedActions++
                }

                val refType = node.desc.substring(1, node.desc.length - 1)
                val writeNode = locateRefWrite(node, instructions, refType)

                // It may have been changed when locating the write node!
                if (node.desc.contains(refType)) {
                    val descriptor = getDescriptorForRefType(refType)!!
                    node.desc = descriptor
                }

                var hasRead = false

                if (next.opcode == Opcodes.GETFIELD && (next as FieldInsnNode).owner == refType) {
                    hasRead = true
                    modifyRead(next, instructions, writeNode)
                    allowedActions--
                }

                if (allowedActions > 0) {
                    // Written to in the future.
                    if (writeNode == null) {
                        logger.error("Couldn't locate write node for $node")
                        return
                    }

                    copyReadWrite(writeNode, node)

                    // Hack for prefix increment operator. probably not great :)
                    val possibleRead = writeNode.next ?: continue
                    if (possibleRead.opcode == Opcodes.GETFIELD && (possibleRead as FieldInsnNode).owner == refType && allowedActions > 0) {
                        copyReadWrite(possibleRead, node)
                        hasRead = true
                    }

                    if (!hasRead) {
                        val prev = node.previous
                        if (prev.opcode == Opcodes.ALOAD && prev is VarInsnNode) {
                            if (node.name.substring(1).matches("local\\d+".toRegex()))
                                instructions.remove(prev)
                        }

                        instructions.remove(node)
                    }
                }
            }
        }
    }

    private fun modifyRead(readNode: FieldInsnNode, instructions: InsnList, writeNode: FieldInsnNode?) {
        /*
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$mutableObj : Lkotlin/jvm/internal/Ref$ObjectRef;
        GETFIELD kotlin/jvm/internal/Ref$ObjectRef.element : Ljava/lang/Object; <---- READ_NODE
        CHECKCAST net/minecraft/util/IChatComponent <---- ONLY FOR NON-OBJECT ObjectRefs
         */

        if (readNode.owner == "kotlin/jvm/internal/Ref\$ObjectRef") {
            val cast = readNode.next
            if (cast is TypeInsnNode && cast.opcode == Opcodes.CHECKCAST) {
                (readNode.previous as FieldInsnNode).desc = cast.desc
                instructions.remove(cast)
            }
        }

        instructions.remove(readNode)
    }

    private fun getDescriptorForRefType(refType: String): String? {
        return when (refType) {
            "ObjectRef" -> "Ljava/lang/Object;"
            "IntRef" -> "I"
            "ByteRef" -> "B"
            "ShortRef" -> "S"
            "FloatRef" -> "F"
            "DoubleRef" -> "D"
            "LongRef" -> "L"
            "BooleanRef" -> "Z"
            "CharRef" -> "C"
            else -> null
        }
    }

    private fun locateRefWrite(node: FieldInsnNode, instructions: InsnList, refType: String): FieldInsnNode? {
        val analyzer = Analyzer(instructions, codeBlockMethodNode)
        var currentNode: AbstractInsnNode = node

        while (currentNode.next != null) {
            currentNode = currentNode.next

            if (currentNode is FieldInsnNode && currentNode.opcode == Opcodes.PUTFIELD && currentNode.owner == refType) {
                val analyzed = analyzer.analyze(node, currentNode)
                if (analyzed.size == 1 && analyzed.first.descriptor == getDescriptorForRefType(refType.substring(24))) {
                    return currentNode
                }

                if (analyzed.size == 2 && analyzed.last.descriptor.contains(refType)) {
                    if (refType == "kotlin/jvm/internal/Ref\$ObjectRef")
                        node.desc = analyzed.first.descriptor
                    return currentNode
                }
            }
        }

        return null
    }

    private fun copyReadWrite(to: FieldInsnNode, from: FieldInsnNode) {
        /*
        READ/WRITE
        (---LOADING---)
        ALOAD 0
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$testMessagesSent : Lkotlin/jvm/internal/Ref$IntRef; -> ... : I
        DUP -> shift ^^^
        GETFIELD kotlin/jvm/internal/Ref$IntRef.element : I -> delete
        (---ADDING---)
        DUP
        ISTORE 1
        ICONST_1
        IADD
        (---SAVING---)
        PUTFIELD kotlin/jvm/internal/Ref$IntRef.element : I

        vvvvvvvvvvvv

        (---LOADING---)
        ALOAD 0
        DUP
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$testMessagesSent : I
        (---ADDING---)
        DUP
        ISTORE 1
        ICONST_1
        IADD
        (---SAVING---)
        PUTFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$testMessagesSent : I
         */

        /*
        READ/WRITE + WRITE
        ALOAD 0
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$testMessagesSent : Lkotlin/jvm/internal/Ref$IntRef;
        ALOAD 0
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$testMessagesSent2 : Lkotlin/jvm/internal/Ref$IntRef;
        DUP
        GETFIELD kotlin/jvm/internal/Ref$IntRef.element : I
        DUP
        ISTORE 1
        ICONST_1
        IADD
        PUTFIELD kotlin/jvm/internal/Ref$IntRef.element : I
        ILOAD 1
        ICONST_2
        ISUB
        PUTFIELD kotlin/jvm/internal/Ref$IntRef.element : I
         */

        /*
        READ + WRITE
        ALOAD 0
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$mutableObj : Lkotlin/jvm/internal/Ref$ObjectRef;
        ALOAD 0
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$mutableObj2 : Lkotlin/jvm/internal/Ref$ObjectRef;
        GETFIELD kotlin/jvm/internal/Ref$ObjectRef.element : Ljava/lang/Object;
        CHECKCAST net/minecraft/util/IChatComponent
        PUTFIELD kotlin/jvm/internal/Ref$ObjectRef.element : Ljava/lang/Object;
         */

        /*
        WRITE/READ

        ALOAD 0
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$testMessagesSent : Lkotlin/jvm/internal/Ref$IntRef;
        DUP
        DUP
        GETFIELD kotlin/jvm/internal/Ref$IntRef.element : I
        ICONST_1
        IADD
        PUTFIELD kotlin/jvm/internal/Ref$IntRef.element : I
        GETFIELD kotlin/jvm/internal/Ref$IntRef.element : I
        INVOKEVIRTUAL java/lang/StringBuilder.append (I)Ljava/lang/StringBuilder;

        ALOAD 0
        DUP
        DUP
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$testMessagesSent : I
        ICONST_1
        IADD
        PUTFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$testMessagesSent : I
        GETFIELD kotlin/jvm/internal/Ref$IntRef.element : I
        INVOKEVIRTUAL java/lang/StringBuilder.append (I)Ljava/lang/StringBuilder;
         */

        to.owner = from.owner
        to.name = from.name
        to.desc = from.desc
    }
}
