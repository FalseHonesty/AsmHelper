package me.falsehonesty.asmhelper.dsl.code.modifiers

import me.falsehonesty.asmhelper.printing.prettyString
import me.falsehonesty.asmhelper.printing.verbose
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.BasicInterpreter
import org.objectweb.asm.tree.analysis.BasicValue
import org.objectweb.asm.tree.analysis.Frame
import java.util.*

abstract class Modifier {
    abstract fun modify(instructions: InsnList)

    protected fun wrappedTypeToPrimitive(type: Type): Type = when (type.descriptor) {
        "Ljava/lang/Boolean;" -> Type.BOOLEAN_TYPE
        "Ljava/lang/Byte;" -> Type.BYTE_TYPE
        "Ljava/lang/Short;" -> Type.SHORT_TYPE
        "Ljava/lang/Character;" -> Type.CHAR_TYPE
        "Ljava/lang/Integer;" -> Type.INT_TYPE
        "Ljava/lang/Long;" -> Type.LONG_TYPE
        "Ljava/lang/Float;" -> Type.FLOAT_TYPE
        "Ljava/lang/Double;" -> Type.DOUBLE_TYPE
        else -> type
    }

    protected fun getAnalyzedFrame(node: AbstractInsnNode, methodNode: MethodNode, className: String): Frame<BasicValue> {
        val analyzer = Analyzer(BasicInterpreter())
        analyzer.analyze(className, methodNode)

        var i = -1
        for (frame in analyzer.frames) {
            i++

            if (methodNode.instructions[i] === node) {
                return frame
            }
        }

        throw IllegalArgumentException("Node $node not found in analyzed method $className.${methodNode.name}.")
    }
}

class Analyzer(val instructions: InsnList, val methodNode: MethodNode) {
    /**
     * Starts at [startInsn] inclusive, ends at [endInsn] exclusive.
     */
    fun analyze(startInsn: AbstractInsnNode, endInsn: AbstractInsnNode): Deque<Type> {
        val stack = ArrayDeque<Type>()

        var node = startInsn

        while (node != endInsn) {
            verbose("Analyzing ${node.prettyString()}")

            when (node) {
                is MethodInsnNode -> {
                    repeat(Type.getArgumentTypes(node.desc).size) {
                        verbose("Popping ${stack.poll()} off the stack because it is an argument")
                    }

                    if (node.opcode != Opcodes.INVOKESTATIC) verbose("Popping receiver ${stack.poll()}")

                    val returnType = Type.getReturnType(node.desc)
                    if (returnType != Type.VOID_TYPE) stack.push(returnType)
                    verbose("Pushed return type $returnType")
                }
                is VarInsnNode -> {
                    // TODO, make this load from local var table.
                    if (node.opcode <= 53) stack.push(Type.getObjectType(methodNode.localVariables[node.`var`].desc))
                    else stack.pop()
                }
                is FieldInsnNode -> {
                    when (node.opcode) {
                        Opcodes.GETSTATIC -> stack.push(Type.getType(node.desc))
                        Opcodes.PUTSTATIC -> stack.pop()
                        Opcodes.GETFIELD -> {
                            // since we can start analyzing at any arbitrary node, if there is no receiver on the stack
                            // we will attempt to gracefully handle it.
                            stack.poll()
                            stack.push(Type.getType(node.desc))
                        }
                        Opcodes.PUTFIELD -> {
                            stack.poll()
                            stack.pop()
                        }
                    }
                }
                is IntInsnNode -> stack.push(Type.INT_TYPE)
                is LdcInsnNode -> {
                    when (node.cst) {
                        is String -> stack.push(Type.getObjectType(String::class.java.name.replace(".", "/")))
                        is Int -> stack.push(Type.INT_TYPE)
                        is Long -> stack.push(Type.LONG_TYPE)
                        is Double -> stack.push(Type.DOUBLE_TYPE)
                        is Float -> stack.push(Type.FLOAT_TYPE)
                        is Type -> stack.push(node.cst as Type)
                    }
                }
                is InsnNode -> {
                    when (node.opcode) {
                        in 2..8 -> stack.push(Type.INT_TYPE)
                        in 9..10 -> stack.push(Type.LONG_TYPE)
                        in 11..13 -> stack.push(Type.FLOAT_TYPE)
                        in 14..15 -> stack.push(Type.DOUBLE_TYPE)
                        in 96..115 -> stack.pop()
                        89 -> stack.push(stack.peek())
                    }
                }
                is TypeInsnNode -> {
                    when (node.opcode) {
                        Opcodes.NEW -> stack.push(Type.getObjectType(node.desc))
                        Opcodes.CHECKCAST -> {
                            stack.pop()
                            stack.push(Type.getObjectType(node.desc))
                        }
                    }
                }
            }

            verbose("Stack after analyzation frame looks like $stack")

            node = node.next
        }

        return stack
    }
}
