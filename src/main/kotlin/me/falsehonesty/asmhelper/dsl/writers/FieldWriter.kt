package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList

class FieldWriter(
    className: String,
    private val fieldName: String,
    private val fieldDesc: String,
    private val initialValue: Any?,
    private val initializer: InsnList?,
    private val initializerDescriptor: String?,
    private vararg val accessTypes: AccessType
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        classNode.fields.add(FieldNode(
            accessTypes.fold(0) { acc, accessType -> acc or accessType.opcode },
            fieldName,
            fieldDesc,
            null,
            initialValue
        ))

        if (initializer != null && initializerDescriptor != null) {
            val insns = classNode.methods
                .find { it.name == "<init>" && it.desc == initializerDescriptor }
                ?.instructions ?: return

            insns.insertBefore(insns.last.previous, initializer)
        }
    }

    override fun toString(): String {
        return "AsmWriter{className=$className, fieldName=$fieldName}"
    }

    class Builder {
        var className: String? = null
        var accessTypes: List<AccessType> = listOf()
        var fieldName: String? = null
        var fieldDesc: String? = null
        var initialValue: Any? = null
        var initializerDescriptor: String? = null
        var initializer: InsnList? = null

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return FieldWriter(
                className ?: throw IllegalStateException("className must not be null"),
                fieldName ?: throw IllegalStateException("fieldName must not be null"),
                fieldDesc ?: throw IllegalStateException("fieldDesc must not be null"),
                initialValue,
                initializer,
                initializerDescriptor,
                *accessTypes.toTypedArray()
            )
        }

        fun initializer(config: InsnListBuilder.() -> Unit) {
            val builder = InsnListBuilder()
            builder.config()

            this.initializer = builder.build()
        }
    }
}
