package me.falsehonesty.asmhelper.dsl

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode

class AsmWriter(val className: String, val methodName: String, val at: At, val insnList: InsnList, val methodType: MethodType) {
    fun transform(classNode: ClassNode) {
        classNode.methods
            .find { FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, it.name, it.desc) == methodName }
            ?.let {
                when (methodType) {
                    MethodType.INJECT -> injectInsnList(it)
                }
            }
    }

    private fun injectInsnList(method: MethodNode) {
        var node = when (at.value) {
            InjectionPoint.HEAD -> method.instructions.first
            InjectionPoint.TAIL -> method.instructions.last
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
        return "AsmWriter{className=$className, methodName=$methodName, at=$at"
    }

    class Builder(private val type: MethodType) {
        var className: String? = null
        var methodName: String? = null
        var at: At? = null
        var insnListData: InsnList? = null

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return AsmWriter(
                className ?: throw IllegalStateException("className must NOT be null."),
                methodName ?: throw IllegalStateException("methodName must NOT be null."),
                at ?: throw IllegalStateException("at must NOT be null."),
                insnListData ?: throw IllegalStateException("insnListData must NOT be null."),
                type
            )
        }
    }
}
