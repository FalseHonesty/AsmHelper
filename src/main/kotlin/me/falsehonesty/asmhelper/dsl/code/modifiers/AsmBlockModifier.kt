package me.falsehonesty.asmhelper.dsl.code.modifiers

import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

class AsmBlockModifier(val targetMethodNode: MethodNode) : Modifier() {
    override fun modify(instructions: InsnList) {
        for (node in instructions) {
            if (node is MethodInsnNode && node.opcode == Opcodes.INVOKESTATIC && node.name == "asm" && node.owner == "me/falsehonesty/asmhelper/dsl/writers/InjectWriterKt") {
                modifyAsmBlock(node, instructions)
            }
        }
    }

    private fun modifyAsmBlock(node: MethodInsnNode, instructions: InsnList) {
        /*
        NEW me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$2
        DUP
        ALOAD 0
        INVOKESPECIAL me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$2.<init> (Lme/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1;)V
        CHECKCAST kotlin/jvm/functions/Function1
        INVOKESTATIC me/falsehonesty/asmhelper/dsl/writers/InjectWriterKt.asm (Lkotlin/jvm/functions/Function1;)V
         */

        val bytecodeClassName = (node.previous.previous as FieldInsnNode).owner
        val bytecodeClass = Class.forName(bytecodeClassName.replace("/", "."))

        val constr = bytecodeClass.declaredConstructors.first()
        constr.isAccessible = true
        val asmLambda = constr.newInstance()
        val invokeMethod = bytecodeClass.declaredMethods.first { it.isSynthetic }
        invokeMethod.isAccessible = true

        val builder = InsnListBuilder(targetMethodNode)
        invokeMethod.invoke(asmLambda, builder)

        instructions.remove(node.previous)
        instructions.remove(node.previous)

        instructions.insertBefore(node, builder.build())

        instructions.remove(node)
    }
}