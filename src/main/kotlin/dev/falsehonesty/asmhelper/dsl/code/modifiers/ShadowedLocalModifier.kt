package dev.falsehonesty.asmhelper.dsl.code.modifiers

import dev.falsehonesty.asmhelper.printing.prettyString
import dev.falsehonesty.asmhelper.printing.verbose
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.VarInsnNode

class ShadowedLocalModifier(codeBlockClass: String) : ShadowedModifier(codeBlockClass) {
    override fun modifyFieldNode(instructions: InsnList, node: FieldInsnNode, shadowedName: String) {
        if (shadowedName.matches("local\\d+".toRegex())) {
            val localNumber = shadowedName.substring("local".length).toInt()

            verbose("Found shadowed local referencing local $localNumber")

            val prev = node.previous

            if (prev !is VarInsnNode)
                return



            if (node.opcode == Opcodes.GETFIELD) {
                val opcode = when (node.desc) {
                    "I" -> Opcodes.ILOAD
                    "B" -> Opcodes.ILOAD
                    "S" -> Opcodes.ILOAD
                    "C" -> Opcodes.ILOAD
                    "Z" -> Opcodes.ILOAD
                    "L" -> Opcodes.LLOAD
                    "F" -> Opcodes.FLOAD
                    "D" -> Opcodes.DLOAD
                    else -> Opcodes.ALOAD
                }

                val prevString = prev.prettyString().trim()
                prev.`var` = localNumber
                prev.opcode = opcode

                verbose(prev.previous.prettyString())
                verbose("$prevString --> ${prev.prettyString().trim()}")
                verbose("- ${node.prettyString()}")

                instructions.remove(node)
            } else if (node.opcode == Opcodes.PUTFIELD) {
                val opcode = when (node.desc) {
                    "I" -> Opcodes.ISTORE
                    "B" -> Opcodes.ISTORE
                    "S" -> Opcodes.ISTORE
                    "C" -> Opcodes.ISTORE
                    "Z" -> Opcodes.ISTORE
                    "L" -> Opcodes.LSTORE
                    "F" -> Opcodes.FSTORE
                    "D" -> Opcodes.DSTORE
                    else -> Opcodes.ASTORE
                }

                instructions.insert(node, VarInsnNode(opcode, localNumber))
                instructions.remove(node)
            }
        }
    }
}
