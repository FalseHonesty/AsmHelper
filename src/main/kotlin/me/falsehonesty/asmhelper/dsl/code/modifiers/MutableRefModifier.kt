package me.falsehonesty.asmhelper.dsl.code.modifiers

import me.falsehonesty.asmhelper.printing.logger
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

                if (next.opcode == Opcodes.GETFIELD && (next as FieldInsnNode).owner == refType) {
                    modifyRead(node, instructions)
                    allowedActions--
                }

                if (allowedActions > 0) {
                    // Written to in the future.
                    val writeNode = locateRefWrite(node, instructions, refType) ?: run {
                        logger.error("Couldn't locate write node for $node")
                        return
                    }

                    copyReadWrite(writeNode, node)

                    // Hack for prefix increment operator. probably not great :)
                    val possibleRead = writeNode.next ?: continue
                    if (possibleRead.opcode == Opcodes.GETFIELD && (possibleRead as FieldInsnNode).owner == refType && allowedActions > 0) {
                        copyReadWrite(possibleRead, node)
                    }
                }
            }
        }
    }

    private fun modifyRead(node: FieldInsnNode, instructions: InsnList) {
        /*
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1.$mutableObj : Lkotlin/jvm/internal/Ref$ObjectRef;
        GETFIELD kotlin/jvm/internal/Ref$ObjectRef.element : Ljava/lang/Object;
        CHECKCAST net/minecraft/util/IChatComponent
         */

        val refType = node.desc.substring(25, node.desc.length - 1)
        if (refType == "ObjectRef") {
            instructions.remove(node.next)
            val cast = node.next
            if (cast is TypeInsnNode && cast.opcode == Opcodes.CHECKCAST) {
                node.desc = cast.desc
                instructions.remove(cast)
            } else {
                node.desc = "Ljava/lang/Object;"
            }
        } else {
            val descriptor = getDescriptorForRefType(refType)
            if (descriptor != null) {
                instructions.remove(node.next)
                node.desc = descriptor
            }
        }
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
                if (analyzed.size == 1 && analyzed.first.descriptor == getDescriptorForRefType(refType.substring(24)))
                    return currentNode
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
