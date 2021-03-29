package me.falsehonesty.asmhelper.dsl.code.modifiers

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodInsnNode

class CodeBlockShortcutModifier : Modifier() {
    override fun modify(instructions: InsnList) {
        for (node in instructions) {
            if (node is FieldInsnNode && node.owner == "me/falsehonesty/asmhelper/dsl/code/CodeBlock" && node.name == "Companion")
                instructions.remove(node)
            else if (node is MethodInsnNode && node.opcode == Opcodes.INVOKEVIRTUAL && node.owner == "me/falsehonesty/asmhelper/dsl/code/CodeBlock\$Companion")
                modifyShortcut(node, instructions)
        }
    }

    private fun modifyShortcut(node: MethodInsnNode, instructions: InsnList) {
        when (node.name) {
            "methodReturn" -> modifyReturnShortcut(node, instructions, Opcodes.RETURN)
            "aReturn" -> modifyReturnShortcut(node, instructions, Opcodes.ARETURN)
            "iReturn" -> modifyReturnShortcut(node, instructions, Opcodes.IRETURN)
            "lReturn" -> modifyReturnShortcut(node, instructions, Opcodes.LRETURN)
            "fReturn" -> modifyReturnShortcut(node, instructions, Opcodes.FRETURN)
            "dReturn" -> modifyReturnShortcut(node, instructions, Opcodes.DRETURN)
        }
    }

    private fun modifyReturnShortcut(node: MethodInsnNode, instructions: InsnList, returnType: Int) {
        instructions.insert(node, InsnNode(returnType))
        instructions.remove(node)
    }
}
