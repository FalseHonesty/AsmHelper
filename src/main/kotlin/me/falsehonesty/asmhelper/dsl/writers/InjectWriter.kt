package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.dsl.At
import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode

class InjectWriter(
    className: String,
    private val methodName: String,
    private val methodDesc: String,
    private val at: At,
    private val insnList: InsnList
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        classNode.methods
            .find {
                it.desc == methodDesc && AsmHelper.remapper.remapMethodName(classNode.name, methodName, methodDesc) == it.name
            }
            ?.let { injectInsnList(it) }
    }

    private fun injectInsnList(method: MethodNode) {
        val nodes = at.getTargetedNodes(method)

        nodes.forEach { insertToNode(method, it) }
    }

    private fun insertToNode(method: MethodNode, node: AbstractInsnNode) {
        var newNode = node

        if (at.shift < 0) {
            repeat(-at.shift) {
                newNode = node.previous
            }
        } else if (at.shift > 0) {
            repeat(at.shift) {
                newNode = node.next
            }
        }

        if (at.before) {
            method.instructions.insertBefore(newNode, insnList)
        } else {
            method.instructions.insert(newNode, insnList)
        }
    }

    override fun toString(): String {
        return "AsmWriter{className=$className, methodName=$methodName, methodDesc=$methodDesc, at=$at}"
    }

    class Builder {
        var className: String? = null
        var methodName: String? = null
        var methodDesc: String? = null
        var at: At? = null
        var insnListData: InsnList? = null

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return InjectWriter(
                className ?: throw IllegalStateException("className must NOT be null."),
                methodName ?: throw IllegalStateException("methodName must NOT be null."),
                methodDesc ?: throw IllegalStateException("methodDesc must NOT be null."),
                at ?: throw IllegalStateException("at must NOT be null."),
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
