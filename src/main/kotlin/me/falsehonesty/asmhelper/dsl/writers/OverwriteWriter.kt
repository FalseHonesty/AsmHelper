package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode

class OverwriteWriter(
    className: String,
    private val methodName: String,
    private val methodDesc: String,
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
            ?.let { overwriteMethod(it) }
    }

    private fun overwriteMethod(node: MethodNode) {
        node.instructions.clear()
        node.exceptions.clear()
        node.tryCatchBlocks.clear()

        val builder = InsnListBuilder(node)
        builder.insnList()

        node.maxLocals = Type.getArgumentTypes(node.desc).size + (if (node.access and Opcodes.ACC_STATIC == 0) 1 else 0)

        node.instructions.add(builder.build())
    }

    override fun toString(): String {
        return "OverwriteWriter{className=$className, methodName=$methodName}"
    }

    class Builder {
        lateinit var className: String
        lateinit var methodName: String
        lateinit var methodDesc: String
        lateinit var insnListData: InsnListBuilder.() -> Unit

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return OverwriteWriter(
                className,
                methodName,
                methodDesc,
                insnListData
            )
        }

        fun insnList(config: InsnListBuilder.() -> Unit) {
            this.insnListData = config
        }
    }
}
