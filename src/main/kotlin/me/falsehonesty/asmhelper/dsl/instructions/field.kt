package me.falsehonesty.asmhelper.dsl.instructions

import me.falsehonesty.asmhelper.AsmHelper
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode

enum class FieldAction(val opcode: Int) {
    GET_STATIC(Opcodes.GETSTATIC),
    PUT_STATIC(Opcodes.PUTSTATIC),
    GET_FIELD(Opcodes.GETFIELD),
    PUT_FIELD(Opcodes.PUTFIELD)
}

fun InsnListBuilder.field(action: FieldAction, descriptor: Descriptor) = this.field(action, descriptor.owner, descriptor.name, descriptor.desc)

fun InsnListBuilder.field(action: FieldAction, owner: String, name: String, desc: String) {
    //TODO: Create our own utility so we can go from deobf -> obf
    val realName = if (!AsmHelper.obfuscated) FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)
    else name

    insnList.add(FieldInsnNode(
        action.opcode,
        owner,
        realName,
        desc
    ))
}

fun InsnListBuilder.getLocalField(descriptor: Descriptor) {
    aload(0)
    field(FieldAction.GET_FIELD, descriptor)
}

fun InsnListBuilder.updateLocalField(descriptor: Descriptor, updater: InsnListBuilder.() -> Unit) {
    aload(0)
    getLocalField(descriptor)

    this.updater()

    field(FieldAction.PUT_FIELD, descriptor)
}

fun InsnListBuilder.setLocalField(descriptor: Descriptor) {
    aload(0)
    field(FieldAction.PUT_FIELD, descriptor)
}