package dev.falsehonesty.asmhelper.dsl.code.modifiers

import dev.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import dev.falsehonesty.asmhelper.printing.prettyString
import dev.falsehonesty.asmhelper.printing.verbose
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

class AsmBlockModifier(val targetMethodNode: MethodNode) : Modifier() {
    override fun modify(instructions: InsnList) {
        for (node in instructions) {
            if (node is MethodInsnNode && node.opcode == Opcodes.INVOKEVIRTUAL && node.name == "asm" && node.owner == "dev/falsehonesty/asmhelper/dsl/code/CodeBlock\$Companion") {
                modifyAsmBlock(node, instructions)
            }
        }
    }

    private fun modifyAsmBlock(node: MethodInsnNode, instructions: InsnList) {
        /*
        GETSTATIC dev/falsehonesty/asmhelper/dsl/code/CodeBlock.Companion : Ldev/falsehonesty/asmhelper/dsl/code/CodeBlock$Companion;
        GETSTATIC dev/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1$1.INSTANCE : Ldev/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1$1;
        CHECKCAST kotlin/jvm/functions/Function1
        INVOKEVIRTUAL dev/falsehonesty/asmhelper/dsl/code/CodeBlock$Companion.asm (Lkotlin/jvm/functions/Function1;)V
         */

        /*
        GETSTATIC dev/falsehonesty/asmhelper/dsl/code/CodeBlock.Companion : Ldev/falsehonesty/asmhelper/dsl/code/CodeBlock$Companion;
        NEW dev/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1$1
        DUP
        ALOAD 0
        INVOKESPECIAL dev/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1$1.<init> (Ldev/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1;)V
        CHECKCAST kotlin/jvm/functions/Function1
        INVOKEVIRTUAL dev/falsehonesty/asmhelper/dsl/code/CodeBlock$Companion.asm (Lkotlin/jvm/functions/Function1;)V
         */

        val bytecodeClassName = when (val lambdaValue = node.previous.previous) {
            is FieldInsnNode -> {
                lambdaValue.owner
            }
            is MethodInsnNode -> {
                throw IllegalArgumentException("Inline asm blocks can't capture locals")
            }
            else -> throw IllegalStateException("$lambdaValue isn't expected")
        }

        val bytecodeClass = Class.forName(bytecodeClassName.replace("/", "."))

        val constr = bytecodeClass.declaredConstructors.first()
        constr.isAccessible = true
        val asmLambda = constr.newInstance()
        val invokeMethod = bytecodeClass.declaredMethods.first {
            it.parameters.size == 1 && it.parameters.first().type == InsnListBuilder::class.java
        }
        invokeMethod.isAccessible = true

        val builder = InsnListBuilder(targetMethodNode)
        invokeMethod.invoke(asmLambda, builder)
        val insns = builder.build()

        verbose("- ${node.previous.prettyString()}")
        instructions.remove(node.previous)
        verbose("- ${node.previous.prettyString()}")
        instructions.remove(node.previous)

        verbose("- ${node.prettyString()}")
        insns.prettyString().split("\n").forEach {
            if (it.isNotBlank()) verbose("+ $it")
        }

        instructions.insertBefore(node, insns)
        instructions.remove(node)
    }
}
