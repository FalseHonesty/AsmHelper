package dev.falsehonesty.asmhelper.dsl.writers

import dev.falsehonesty.asmhelper.dsl.AsmWriter
import dev.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList

class FieldWriter(
    className: String,
    private val fieldName: String,
    private val fieldDesc: String,
    private val initialValue: Any?,
    private val accessTypes: List<AccessType>
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        classNode.fields.add(FieldNode(
            accessTypes.fold(0) { acc, accessType -> acc or accessType.opcode },
            fieldName,
            fieldDesc,
            null,
            initialValue
        ))
    }

    override fun toString(): String {
        return "FieldWriter{className=$className, fieldName=$fieldName}"
    }

    class Builder {
        var className: String? = null
        var accessTypes: List<AccessType> = listOf()
        var fieldName: String? = null
        var fieldDesc: String? = null
        var initialValue: Any? = null

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return FieldWriter(
                className ?: throw IllegalStateException("className must not be null"),
                fieldName ?: throw IllegalStateException("fieldName must not be null"),
                fieldDesc ?: throw IllegalStateException("fieldDesc must not be null"),
                initialValue,
                accessTypes
            )
        }
    }
}
