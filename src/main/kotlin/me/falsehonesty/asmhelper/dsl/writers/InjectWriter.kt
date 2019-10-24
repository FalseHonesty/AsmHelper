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
    private val insnList: InsnListBuilder.() -> Unit
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
            ?.let { injectInsnList(it) }
    }

    private fun injectInsnList(method: MethodNode) {
        val nodes = at.getTargetedNodes(method)

        val builder = InsnListBuilder(method)
        builder.insnList()

        nodes.forEach { insertToNode(method, it, builder.build()) }
    }

    private fun insertToNode(method: MethodNode, node: AbstractInsnNode, insnList: InsnList) {
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
        return "InjectWriter{className=$className, methodName=$methodName, methodDesc=$methodDesc, at=$at}"
    }

    class Builder {
        lateinit var className: String
        lateinit var methodName: String
        lateinit var methodDesc: String
        lateinit var at: At
        lateinit var insnListBuilder: InsnListBuilder.() -> Unit

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return InjectWriter(
                className, methodName, methodDesc,
                at, insnListBuilder
            )
        }

        fun insnList(config: InsnListBuilder.() -> Unit) {
            this.insnListBuilder = config
        }
    }
}
