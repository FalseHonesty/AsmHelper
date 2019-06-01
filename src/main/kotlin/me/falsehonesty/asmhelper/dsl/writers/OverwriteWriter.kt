package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode

class OverwriteWriter(
    className: String,
    private val methodName: String,
    private val methodDesc: String,
    private val insnList: InsnList
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        classNode.methods
            .find {
                it.desc == methodDesc && AsmHelper.remapper.remapMethodName(classNode.name, methodName, methodDesc) == it.name
            }
            ?.let { overwriteMethod(it) }
    }

    private fun overwriteMethod(node: MethodNode) {
        node.instructions.clear()
        node.instructions.add(insnList)
    }

    override fun toString(): String {
        return "OverwriteWriter{className=$className, methodName=$methodName}"
    }

    class Builder {
        var className: String? = null
        var methodName: String? = null
        var methodDesc: String? = null
        var insnListData: InsnList? = null

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return OverwriteWriter(
                className ?: throw IllegalStateException("className must NOT be null."),
                methodName ?: throw IllegalStateException("methodName must NOT be null."),
                methodDesc ?: throw IllegalStateException("methodDesc must NOT be null."),
                insnListData ?: throw IllegalStateException("insnListData must NOT be null.")
            )
        }

        fun insnList(config: InsnListBuilder.() -> Unit) {
            val builder = InsnListBuilder()
            builder.config()

            this.insnListData = builder.build()
        }
    }
}
