package me.falsehonesty.asmhelper.dsl.code.modifiers

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.VarInsnNode

class ShadowedLocalModifier(codeBlockClass: String) : ShadowedModifier(codeBlockClass) {
    override fun modifyFieldNode(instructions: InsnList, node: FieldInsnNode, shadowedName: String) {
        if (shadowedName.matches("local\\d+".toRegex())) {
            val localNumber = shadowedName.substring("local".length).toInt()

            val prev = node.previous

            if (prev is VarInsnNode) {
                instructions.remove(node)

                prev.`var` = localNumber

                return
            }
        }
    }
}