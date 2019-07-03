package me.falsehonesty.asmhelper.dsl.code

import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

class InjectCodeBuilder(codeClassNode: ClassNode, val targetClassNode: ClassNode, val targetMethodNode: MethodNode) :
    CodeBuilder(codeClassNode) {
    private val codeBlockMethod = getMethodNode()
    private val codeBlockInsns: InsnList = codeBlockMethod.instructions
    private val codeBlockClass = classNode.name

    fun codeBlockToInstructions(): InsnList {
        for (node in codeBlockInsns) {
            when (node) {
                is VarInsnNode -> transformVarNodes(node)
                is FieldInsnNode -> transformFieldNodes(node)
                is InsnNode -> transformGeneralNodes(node)
                is MethodInsnNode -> transformMethodNodes(node)
            }
        }

        return codeBlockInsns
    }

    private fun transformVarNodes(node: VarInsnNode) {
        if (node.`var` != 0) {
            node.`var` += (targetMethodNode.maxLocals - 1)
        }
    }

    private fun transformFieldNodes(node: FieldInsnNode) {
        if (node.owner != codeBlockClass) return

        val shadowedName = node.name.substring(1)

        // TODO: Remap.

        /*
        GETFIELD me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1.$getChatOpen : Lkotlin/jvm/functions/Function0;
        INVOKEINTERFACE kotlin/jvm/functions/Function0.invoke ()Ljava/lang/Object; (itf)
        CHECKCAST java/lang/Boolean
         */

        val next = node.next

        if (next is MethodInsnNode && next.itf && next.name == "invoke" && node.desc.substring(1, node.desc.length - 1) == next.owner) {
            // We're pretty sure this is a shadowed method.

            val methodArguments = Type.getArgumentTypes(next.desc)

            // Now we need to know the return type.
            val returnTypeIndicator = next.next

            val returnType = if (returnTypeIndicator is TypeInsnNode) {
                // This must be a CHECKCAST, so we can snag the return type from here.
                val rawType = Type.getObjectType(returnTypeIndicator.desc)

                when (rawType.descriptor) {
                    "Ljava/lang/Boolean;" -> Type.BOOLEAN_TYPE
                    "Ljava/lang/Byte;" -> Type.BYTE_TYPE
                    "Ljava/lang/Short;" -> Type.SHORT_TYPE
                    "Ljava/lang/Character;" -> Type.CHAR_TYPE
                    "Ljava/lang/Integer;" -> Type.INT_TYPE
                    "Ljava/lang/Long;" -> Type.LONG_TYPE
                    "Ljava/lang/Float;" -> Type.FLOAT_TYPE
                    "Ljava/lang/Double;" -> Type.DOUBLE_TYPE
                    else -> rawType
                }
            } else {
                // This ought to be a POP (to discard the return value) meaning it must be void/unit.
                Type.VOID_TYPE
            }

            val syntheticMethodDesc = Type.getMethodDescriptor(returnType, *methodArguments)

            // TODO: Remap?

            // Now we want to remove all these instructions to replace them with a valid method call to the shadowed method.
            // TODO: Implement super shadowing
            val methodCall = MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                targetClassNode.name,
                shadowedName,
                syntheticMethodDesc,
                false
            )

            codeBlockInsns.remove(returnTypeIndicator)
            codeBlockInsns.remove(next)

            codeBlockInsns.insertBefore(node, methodCall)

            val nextCall = node.next

            if (nextCall is MethodInsnNode && nextCall.owner.startsWith("java/lang/") && nextCall.name.endsWith("Value") && nextCall.desc.startsWith("()")) {
                // We're pretty sure this is a Wrapper -> Primitive call, so we need to remove it!
                codeBlockInsns.remove(nextCall)
            }

            codeBlockInsns.remove(node)

            return
        }

        node.owner = targetClassNode.name
        node.name = shadowedName
    }

    private fun transformMethodNodes(node: MethodInsnNode) {
        /*
        NEW me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$2
        DUP
        ALOAD 0
        INVOKESPECIAL me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$2.<init> (Lme/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1;)V
        CHECKCAST kotlin/jvm/functions/Function1
        INVOKESTATIC me/falsehonesty/asmhelper/dsl/writers/InjectWriterKt.asm (Lkotlin/jvm/functions/Function1;)V
         */

        if (node.opcode == Opcodes.INVOKESTATIC && node.name == "asm" && node.owner == "me/falsehonesty/asmhelper/dsl/writers/InjectWriterKt") {
            val bytecodeClassName = (node.previous.previous as FieldInsnNode).owner
            val bytecodeClass = Class.forName(bytecodeClassName.replace("/", "."))

            val constr = bytecodeClass.declaredConstructors.first()
            constr.isAccessible = true
            val asmLambda = constr.newInstance()
            val invokeMethod = bytecodeClass.declaredMethods.first { it.isSynthetic }
            invokeMethod.isAccessible = true

            val builder = InsnListBuilder(targetMethodNode)
            invokeMethod.invoke(asmLambda, builder)

            codeBlockInsns.remove(node.previous)
            codeBlockInsns.remove(node.previous)

            codeBlockInsns.insertBefore(node, builder.build())

            codeBlockInsns.remove(node)
        }
    }

    private fun transformGeneralNodes(node: InsnNode) {
        when (node.opcode) {
            Opcodes.RETURN -> codeBlockInsns.remove(node)
        }
    }
}