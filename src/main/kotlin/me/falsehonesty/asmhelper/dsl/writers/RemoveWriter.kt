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
    private val numberToRemove: Int
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        classNode.methods
            .find {
                it.desc == methodDesc && AsmHelper.remapper.remapMethodName(
                    classNode.name,
                    methodName,
                    methodDesc
                ) == it.name
            }
            ?.let { removeInsns(it) }
    }

    private fun removeInsns(method: MethodNode) {
        val nodes = at.getTargetedNodes(method)

        nodes.forEach { node ->
            var toRemove = node

            repeat(numberToRemove) {
                val newNext = node.next
                method.instructions.remove(toRemove)
                toRemove = newNext
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

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return RemoveWriter(
                className ?: throw IllegalStateException("className must NOT be null."),
                methodName ?: throw IllegalStateException("methodName must NOT be null."),
                methodDesc ?: throw IllegalStateException("methodDesc must NOT be null."),
                at ?: throw IllegalStateException("at must NOT be null."),
                numberToRemove
            )
        }
    }
}
