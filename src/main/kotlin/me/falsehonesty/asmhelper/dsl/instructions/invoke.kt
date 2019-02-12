package me.falsehonesty.asmhelper.dsl.instructions

import me.falsehonesty.asmhelper.AsmHelper
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodInsnNode

class InvokeBuilder {
    var type: InvokeType? = null
    var owner: String? = null
    var name: String? = null
    var desc: String? = null

    @Throws(IllegalStateException::class)
    fun build(): MethodInsnNode {
        val realName = if (!AsmHelper.obfuscated) FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)
                    else name

        return MethodInsnNode(
            type?.opcode ?: throw IllegalStateException("type must NOT be null."),
            owner ?: throw IllegalStateException("owner must NOT be null."),
            realName ?: throw IllegalStateException("name must NOT be null."),
            desc ?: throw IllegalStateException("desc must NOT be null."),
            type == InvokeType.INTERFACE
        )
    }
}

enum class InvokeType(val opcode: Int) {
    VIRTUAL(Opcodes.INVOKEVIRTUAL),
    SPECIAL(Opcodes.INVOKESPECIAL),
    STATIC(Opcodes.INVOKESTATIC),
    INTERFACE(Opcodes.INVOKEINTERFACE)
}

fun InsnListBuilder.method(config: InvokeBuilder.() -> Unit) {
    val builder = InvokeBuilder()
    builder.config()

    insnList.add(builder.build())
}
