package me.falsehonesty.asmhelper.dsl.instructions

import me.falsehonesty.asmhelper.AsmHelper
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodInsnNode

enum class InvokeType(val opcode: Int) {
    VIRTUAL(Opcodes.INVOKEVIRTUAL),
    SPECIAL(Opcodes.INVOKESPECIAL),
    STATIC(Opcodes.INVOKESTATIC),
    INTERFACE(Opcodes.INVOKEINTERFACE)
}

fun InsnListBuilder.invoke(type: InvokeType, descriptor: Descriptor) = this.invoke(type, descriptor.owner, descriptor.name, descriptor.desc)

fun InsnListBuilder.invoke(type: InvokeType, owner: String, name: String, desc: String) {
    val realName =
        if (!AsmHelper.obfuscated) FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc) else name

    insnList.add(MethodInsnNode(
        type.opcode,
        owner,
        realName,
        desc,
        type == InvokeType.INTERFACE
    ))
}