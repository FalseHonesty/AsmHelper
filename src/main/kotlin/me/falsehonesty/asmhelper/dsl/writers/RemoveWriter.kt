package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.dsl.At
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

class RemoveWriter(
    className: String,
    private val methodName: String,
    private val methodDesc: String,
    private val at: At,
    private val numberToRemove: Int,
    private val methodMaps: Map<String, String>
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        classNode.methods
            .find {
                val remapped = AsmHelper.remapper.remapMethodName(classNode.name, it.name, it.desc)
                val remappedDesc = AsmHelper.remapper.remapDesc(it.desc)

                remappedDesc == methodDesc && (remapped == methodName || methodMaps[remapped] == methodName)
            }
            ?.let { removeInsns(it) }
    }

    private fun removeInsns(method: MethodNode) {
        val nodes = at.getTargetedNodes(method)

        nodes.forEach { node ->
            var toDelete = node

            repeat(numberToRemove) {
                val tmpNode = toDelete.next
                method.instructions.remove(toDelete)
                toDelete = tmpNode ?: return@forEach
            }
        }
    }

    override fun toString(): String {
        return "RemoveWriter{className=$className,at=$at,numToRem=$numberToRemove}"
    }

    class Builder {
        var className: String? = null
        var methodName: String? = null
        var methodDesc: String? = null
        var at: At? = null
        var numberToRemove: Int = 1
        var methodMaps = mapOf<String, String>()

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return RemoveWriter(
                className ?: throw IllegalStateException("className must NOT be null."),
                methodName ?: throw IllegalStateException("methodName must NOT be null."),
                methodDesc ?: throw IllegalStateException("methodDesc must NOT be null."),
                at ?: throw IllegalStateException("at must NOT be null."),
                numberToRemove,
                methodMaps
            )
        }
    }
}
