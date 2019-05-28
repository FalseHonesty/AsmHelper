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


/**
 * Calls a specified method.
 *
 * @param owner the name of the owning class. Packages should be separated using slashes.
 * @param name the name of the method to call.
 * @param desc the method's signature. Ex. (F)Lnet/minecraft/util/Vec3;
 */
fun InsnListBuilder.invoke(type: InvokeType, owner: String, name: String, desc: String) {
    val realName =
        if (!AsmHelper.deobf) FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc) else name

    insnList.add(MethodInsnNode(
        type.opcode,
        owner,
        realName,
        desc,
        type == InvokeType.INTERFACE
    ))
}