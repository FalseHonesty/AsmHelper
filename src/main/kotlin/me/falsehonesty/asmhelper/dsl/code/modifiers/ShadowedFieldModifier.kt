package me.falsehonesty.asmhelper.dsl.code.modifiers

import me.falsehonesty.asmhelper.printing.prettyString
import me.falsehonesty.asmhelper.printing.verbose
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList

class ShadowedFieldModifier(codeBlockClass: String, val targetClassNode: ClassNode) : ShadowedModifier(codeBlockClass) {
    override fun modifyFieldNode(instructions: InsnList, node: FieldInsnNode, shadowedName: String) {
        verbose("Any fields referencing the code block class at this point must be a normal field.")
        val prevString = node.prettyString().trim()

        node.owner = targetClassNode.name
        node.name = shadowedName

        verbose("$prevString --> ${node.prettyString().trim()}")
    }
}