package me.falsehonesty.asmhelper.dsl.code.modifiers

import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import me.falsehonesty.asmhelper.printing.prettyString
import me.falsehonesty.asmhelper.printing.verbose
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
        GETSTATIC me/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1$1.INSTANCE : Lme/falsehonesty/asmhelper/example/TestClassTransformer$injectCountPrint$1$1$1$1;
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