package dev.falsehonesty.asmhelper.dsl.code.modifiers

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList

abstract class ShadowedModifier(val codeBlockClass: String) : Modifier() {
    override fun modify(instructions: InsnList) {
        for (node in instructions) {
            if (node is FieldInsnNode && node.owner == codeBlockClass) {
                // TODO: Remap shadowed name

                modifyFieldNode(instructions, node, node.name.substring(1))
            }
        }
    }

    abstract fun modifyFieldNode(instructions: InsnList, node: FieldInsnNode, shadowedName: String)
}
