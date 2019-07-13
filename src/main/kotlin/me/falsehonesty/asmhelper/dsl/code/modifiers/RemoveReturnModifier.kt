package me.falsehonesty.asmhelper.dsl.code.modifiers

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnList

class RemoveReturnModifier : Modifier() {
    override fun modify(instructions: InsnList) {
        for (node in instructions) {
            if (node.opcode == Opcodes.RETURN) instructions.remove(node)
        }
    }
}