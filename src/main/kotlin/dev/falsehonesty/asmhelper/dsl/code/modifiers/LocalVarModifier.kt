package dev.falsehonesty.asmhelper.dsl.code.modifiers

import dev.falsehonesty.asmhelper.printing.prettyString
import dev.falsehonesty.asmhelper.printing.verbose
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

class LocalVarModifier(val targetMethodNode: MethodNode) : Modifier() {
    override fun modify(instructions: InsnList) {
        for (node in instructions) {
            if (node is VarInsnNode && node.`var` != 0) {
                val before = node.prettyString().trim()

                node.`var` += (targetMethodNode.maxLocals - 1)

                verbose("$before --> ${node.prettyString().trim()}")
            }
        }
    }
}
