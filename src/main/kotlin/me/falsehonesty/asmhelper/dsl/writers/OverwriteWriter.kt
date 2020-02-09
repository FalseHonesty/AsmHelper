package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import me.falsehonesty.asmhelper.remapping.ForgeRemapper
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
    private val insnList: InsnListBuilder.() -> Unit,
    private val fieldMaps: Map<String, String>,
    private val methodMaps: Map<String, String>
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        AsmHelper.fieldMaps = fieldMaps
        AsmHelper.methodMaps = methodMaps
        classNode.methods
            .find {
                it.desc == methodDesc && AsmHelper.remapper.remapMethodName(classNode.name, it.name, it.desc) == methodName
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
        var fieldMaps = mapOf<String, String>()
        var methodMaps = mapOf<String, String>()

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return OverwriteWriter(
                className, methodName, methodDesc,
                insnListData,
                fieldMaps, methodMaps
            )
        }

        fun insnList(config: InsnListBuilder.() -> Unit) {
            this.insnListData = config
        }
    }
}
