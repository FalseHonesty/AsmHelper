package me.falsehonesty.asmhelper.dsl

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.AsmHelper.logger
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
            }.let { if (value.ordinal != null) listOf(it[value.ordinal]) else it }
            is InjectionPoint.INVOKE -> method.instructions.iterator().asSequence().toList().filter {
                val descriptor = value.descriptor

                if (it is MethodInsnNode) {
                    logger.info("Trying method insn node candidate ${it.owner}.${it.name} ${it.desc}")

                    logger.info("Matches: " +
                            it.desc == descriptor.desc
                            && it.name == descriptor.name
                            && it.owner == descriptor.owner)
                }

                val realName = AsmHelper.remapper.remapMethodName(descriptor.owner, descriptor.name, descriptor.owner)

                it is MethodInsnNode
                        && it.owner == descriptor.owner
                        && realName == descriptor.name
                        && it.desc == descriptor.desc
            }.let { if (value.ordinal != null) listOf(it[value.ordinal]) else it }
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
    class RETURN(val ordinal: Int? = null) : InjectionPoint()

    /**
     * Injects to where an invoke operation is made.
     *
     * Normally, this injects into every invoke opcode that fits this description, however,
     * optionally one can specify the exact opcode to inject to by specifying [ordinal].
     * This value (0 indexed) is the index of the operation you want.
     */
    class INVOKE(val descriptor: Descriptor, val ordinal: Int? = null) : InjectionPoint()

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
