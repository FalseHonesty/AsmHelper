package me.falsehonesty.asmhelper.dsl.instructions

import me.falsehonesty.asmhelper.AsmHelper
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode

class FieldBuilder {
    var type: FieldType? = null
    var owner: String? = null
    var name: String? = null
    var desc: String? = null

    @Throws(IllegalStateException::class)
    fun build(): FieldInsnNode {
        //TODO: Create our own utility so we can go from deobf -> obf
        val realName = if (!AsmHelper.obfuscated) FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)
                        else name

        return FieldInsnNode(
            type?.opcode ?: throw IllegalStateException("type must not be null"),
            owner,
            realName,
            desc
        )
    }
}

enum class FieldType(val opcode: Int) {
    GET_STATIC(Opcodes.GETSTATIC),
    PUT_STATIC(Opcodes.PUTSTATIC),
    GET_FIELD(Opcodes.GETFIELD),
    PUT_FIELD(Opcodes.PUTFIELD)
}

fun InsnListBuilder.field(config: FieldBuilder.() -> Unit) {
    val builder = FieldBuilder()
    builder.config()

    insnList.add(builder.build())
}
