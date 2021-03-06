package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.dsl.AsmWriter
import org.objectweb.asm.tree.ClassNode

class GeneralModificationWriter(
    className: String,
    private val modifyAction: (ClassNode) -> Unit
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        modifyAction(classNode)
    }

    override fun toString(): String {
        return "ModifyWriter{className=$className}"
    }
}
