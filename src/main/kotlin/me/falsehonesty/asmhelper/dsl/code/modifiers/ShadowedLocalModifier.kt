package me.falsehonesty.asmhelper.dsl.code.modifiers

import me.falsehonesty.asmhelper.printing.prettyString
import me.falsehonesty.asmhelper.printing.verbose
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.VarInsnNode

class ShadowedLocalModifier(codeBlockClass: String) : ShadowedModifier(codeBlockClass) {
    override fun modifyFieldNode(instructions: InsnList, node: FieldInsnNode, shadowedName: String) {
        if (shadowedName.matches("local\\d+".toRegex())) {
            val localNumber = shadowedName.substring("local".length).toInt()

            verbose("Found shadowed local referencing local $localNumber")

            val prev = node.previous

            if (prev is VarInsnNode) {
                val prevString = prev.prettyString().trim()
                prev.`var` = localNumber

                verbose(prev.previous.prettyString())
                verbose("$prevString --> ${prev.prettyString().trim()}")
                verbose("- ${node.prettyString()}")

                instructions.remove(node)

                return
            }
        }
    }
}