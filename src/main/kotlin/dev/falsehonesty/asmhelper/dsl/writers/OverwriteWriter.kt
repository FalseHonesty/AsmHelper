package dev.falsehonesty.asmhelper.dsl.writers

import dev.falsehonesty.asmhelper.AsmHelper
import dev.falsehonesty.asmhelper.dsl.AsmWriter
import dev.falsehonesty.asmhelper.dsl.code.CodeBlock
import dev.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

class OverwriteWriter(
    className: String,
    private val methodName: String,
    private val methodDesc: String,
    private val insnListBuilder: (InsnListBuilder.() -> Unit)?,
    private val codeBlockClassName: String?,
    private val fieldMaps: Map<String, String>,
    private val methodMaps: Map<String, String>
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        AsmHelper.fieldMaps = fieldMaps
        AsmHelper.methodMaps = methodMaps
        classNode.methods
            .find {
                val remapped = AsmHelper.remapper.remapMethodName(classNode.name, it.name, it.desc)
                val remappedDesc = AsmHelper.remapper.remapDesc(it.desc)

                remappedDesc == methodDesc && (remapped == methodName || methodMaps[remapped] == methodName)
            }
            ?.let { overwriteMethod(it, classNode) }
    }

    private fun overwriteMethod(method: MethodNode, classNode: ClassNode) {
        method.instructions.clear()
        method.exceptions.clear()
        method.tryCatchBlocks.clear()

        val instructions = transformToInstructions(insnListBuilder, codeBlockClassName, method, classNode) ?: return

        method.maxLocals = Type.getArgumentTypes(method.desc).size + (if (method.access and Opcodes.ACC_STATIC == 0) 1 else 0)
        method.instructions.add(instructions)
    }

    override fun toString(): String {
        return "OverwriteWriter{className=$className, methodName=$methodName}"
    }

    class Builder : AsmWriter.AsmWriterBuilder() {
        lateinit var className: String
        lateinit var methodName: String
        lateinit var methodDesc: String
        private var insnListBuilder: (InsnListBuilder.() -> Unit)? = null
        private var codeBlockClassName: String? = null
        var fieldMaps = mapOf<String, String>()
        var methodMaps = mapOf<String, String>()

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return OverwriteWriter(
                className, methodName, methodDesc,
                insnListBuilder, codeBlockClassName,
                fieldMaps, methodMaps
            )
        }

        fun insnList(config: InsnListBuilder.() -> Unit) {
            this.insnListBuilder = config
        }

        fun codeBlock(code: CodeBlock.() -> Unit) {
            this.codeBlockClassName = code.javaClass.name + "$1"
        }
    }
}
