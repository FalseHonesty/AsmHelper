package me.falsehonesty.asmhelper.dsl.code.modifiers

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList

class ShadowedFieldModifier(codeBlockClass: String, val targetClassNode: ClassNode) : ShadowedModifier(codeBlockClass) {
    override fun modifyFieldNode(instructions: InsnList, node: FieldInsnNode, shadowedName: String) {
        node.owner = targetClassNode.name
        node.name = shadowedName
    }
}