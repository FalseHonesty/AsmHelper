package dev.falsehonesty.asmhelper.dsl

import dev.falsehonesty.asmhelper.AsmHelper
import dev.falsehonesty.asmhelper.dsl.instructions.Descriptor
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
            }.let { if (value.ordinal != null) listOf(it[value.ordinal]) else it }
            is InjectionPoint.INVOKE -> method.instructions.iterator().asSequence().toList().filter {
                val descriptor = value.descriptor

                if (it is MethodInsnNode) {
                    val remappedName = AsmHelper.remapper.remapMethodName(it.owner, it.name, it.desc)
                    val remappedDesc = AsmHelper.remapper.remapDesc(it.desc)
                    val remappedClassName = AsmHelper.remapper.remapClassName(it.owner)

                    remappedClassName == descriptor.owner
                            && (remappedName == descriptor.name ||
                                AsmHelper.methodMaps[remappedName] == descriptor.name ||
                                AsmHelper.methodMaps[descriptor.name] == remappedName)
                            && remappedDesc == descriptor.desc
                } else {
                    false
                }
            }.let {
                if (value.ordinal != null)
                    if (value.ordinal >= it.size) emptyList() else listOf(it[value.ordinal])
                else it
            }
            is InjectionPoint.CUSTOM -> value.finder(method)
        }
    }
}

sealed class InjectionPoint {
    object HEAD : InjectionPoint()

    /**
     * Injects to where a return operation is made.
     *
     * Normally, this injects into every return opcode that fits this description, however,
     * optionally one can specify the exact opcode to inject to by specifying [ordinal].
     * This value (0 indexed) is the index of the operation you want.
     */
    data class RETURN(val ordinal: Int? = null) : InjectionPoint()

    /**
     * Injects to where an invoke operation is made.
     *
     * Normally, this injects into every invoke opcode that fits this description, however,
     * optionally one can specify the exact opcode to inject to by specifying [ordinal].
     * This value (0 indexed) is the index of the operation you want.
     */
    data class INVOKE(val descriptor: Descriptor, val ordinal: Int? = null) : InjectionPoint()

    /**
     * Injects into the very very end of a method, before the final return.
     */
    object TAIL : InjectionPoint()

    /**
     * Allows the user to find their own injection points. The method node
     * to be searched is passed in, and the finder method should return
     * a list of nodes to be injected to. The list can be of size >= 0.
     */
    class CUSTOM(val finder: (MethodNode) -> List<AbstractInsnNode>) : InjectionPoint()
}
