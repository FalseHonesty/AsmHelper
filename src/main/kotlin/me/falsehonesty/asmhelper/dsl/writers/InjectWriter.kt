package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.dsl.At
import me.falsehonesty.asmhelper.dsl.InjectionPoint
import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode

class InjectWriter(
    className: String,
    private val methodName: String,
    private val at: At,
    private val insnList: InsnList
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        classNode.methods
            .find { FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, it.name, it.desc) == methodName }
            ?.let { injectInsnList(it) }
    }

    private fun injectInsnList(method: MethodNode) {
        var node = when (at.value) {
            InjectionPoint.HEAD -> method.instructions.first
            InjectionPoint.TAIL -> method.instructions.last.previous
        }

        if (at.shift < 0) {
            repeat(-at.shift) {
                node = node.previous
            }
        } else if (at.shift > 0) {
            repeat(at.shift) {
                node = node.next
            }
        }

        if (at.before) {
            method.instructions.insertBefore(node, insnList)
        } else {
            method.instructions.insert(node, insnList)
        }
    }

    override fun toString(): String {
        return "AsmWriter{className=$className, methodName=$methodName, at=$at}"
    }

    class Builder {
        var className: String? = null
        var methodName: String? = null
        var at: At? = null
        var insnListData: InsnList? = null

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return InjectWriter(
                className ?: throw IllegalStateException("className must NOT be null."),
                methodName ?: throw IllegalStateException("methodName must NOT be null."),
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
