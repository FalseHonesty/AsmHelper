package me.falsehonesty.asmhelper.dsl.code.modifiers

import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

class LocalVarModifier(val targetMethodNode: MethodNode) : Modifier() {
    override fun modify(instructions: InsnList) {
        for (node in instructions) {
            if (node is VarInsnNode && node.`var` != 0) {
                node.`var` += (targetMethodNode.maxLocals - 1)
            }
        }
    }
}