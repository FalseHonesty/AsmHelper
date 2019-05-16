package me.falsehonesty.asmhelper.dsl

import me.falsehonesty.asmhelper.dsl.instructions.Descriptor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * This utility class helps to specify where some bytecode action should occur.
 *
 * There are numerous [InjectionPoint]s you can utilize, each providing a different abstraction.
 *
 * @param before specifies if the action should occur before (the default) or after the located instruction.
 * @param shift specifies the offset to where the action should occur. This value can be negative or positive. This value always shifts forward, regardless of the value of [before].
 */
data class At(val value: InjectionPoint, val before: Boolean = true, val shift: Int = 0) {
    fun getTargetedNodes(method: MethodNode): List<AbstractInsnNode> {
        return when (value) {
            is InjectionPoint.HEAD -> listOf(method.instructions.first)
            is InjectionPoint.TAIL -> listOf(method.instructions.last.previous)
            is InjectionPoint.RETURN -> method.instructions.iterator().asSequence().toList().filter {
                it.opcode == Opcodes.RETURN
            }
            is InjectionPoint.INVOKE -> method.instructions.iterator().asSequence().toList().filter {
                val descriptor = value.descriptor

                it is MethodInsnNode
                        && it.desc == descriptor.desc
                        && it.name == descriptor.name
                        && it.owner == descriptor.owner
            }
        }
    }
}

sealed class InjectionPoint {
    object HEAD : InjectionPoint()
    object RETURN : InjectionPoint()
    class INVOKE(val descriptor: Descriptor) : InjectionPoint()
    object TAIL : InjectionPoint()
}
