package me.falsehonesty.asmhelper.dsl.instructions

import me.falsehonesty.asmhelper.AsmHelper
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodInsnNode

enum class InvokeType(val opcode: Int) {
    VIRTUAL(Opcodes.INVOKEVIRTUAL),
    SPECIAL(Opcodes.INVOKESPECIAL),
    STATIC(Opcodes.INVOKESTATIC),
    INTERFACE(Opcodes.INVOKEINTERFACE)
}

fun InsnListBuilder.invoke(type: InvokeType, descriptor: Descriptor, arguments: (InsnListBuilder.() -> Unit)? = null)
        = this.invoke(type, descriptor.owner, descriptor.name, descriptor.desc, arguments)


/**
 * Calls a specified method.
 *
 * @param owner the name of the owning class. Packages should be separated using slashes.
 * @param name the name of the method to call.
 * @param desc the method's signature. Ex. (F)Lnet/minecraft/util/Vec3;
 */
fun InsnListBuilder.invoke(type: InvokeType, owner: String, name: String, desc: String, arguments: (InsnListBuilder.() -> Unit)? = null) {
    val realName = AsmHelper.remapper.remapMethodName(owner, name, desc)

    if (arguments != null) {
        val insns = InsnListBuilder()
        insns.arguments()
        insnList.add(insns.build())
    }

    insnList.add(MethodInsnNode(
        type.opcode,
        owner,
        realName,
        desc,
        type == InvokeType.INTERFACE
    ))
}

/**
 * Simple block to segment off each individual argument.
 *
 * Definitely not necessary, it simply runs the code in the lambda immediately.
 */
fun InsnListBuilder.argument(argumentCode: InsnListBuilder.() -> Unit) {
    this.argumentCode()
}