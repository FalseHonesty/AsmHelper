package me.falsehonesty.asmhelper.dsl

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

class InsnListBuilder {
    val insnList = InsnList()

    fun aload(value: Int) {
        insnList.add(VarInsnNode(Opcodes.ALOAD, value))
    }

    fun areturn() {
        insnList.add(InsnNode(Opcodes.ARETURN))
    }

    fun iload(value: Int) {
        insnList.add(VarInsnNode(Opcodes.ILOAD, value))
    }

    fun ireturn() {
        insnList.add(InsnNode(Opcodes.IRETURN))
    }

    fun bipush(value: Int) {
        insnList.add(VarInsnNode(Opcodes.BIPUSH, value))
    }

    fun isub() {
        insnList.add(InsnNode(Opcodes.ISUB))
    }

    fun new(className: String) {
        insnList.add(TypeInsnNode(Opcodes.NEW, className))
    }

    fun dup() {
        insnList.add(InsnNode(Opcodes.DUP))
    }

    fun return_() {
        insnList.add(InsnNode(Opcodes.RETURN))
    }

    fun makeLabel() = LabelNode()

    fun jump(condition: JumpCondition, label: LabelNode) {
        insnList.add(JumpInsnNode(condition.opcode, label))
    }

    fun placeLabel(label: LabelNode) {
        insnList.add(label)
    }

    fun build(): InsnList = insnList
}

enum class JumpCondition(val opcode: Int) {
    EQUAL(Opcodes.IFEQ),
    NOT_EQUAL(Opcodes.IFNE),
    LESS_THAN(Opcodes.IFLT),
    GREATER_OR_EQUAL(Opcodes.IFGE),
    GREATER_THAN(Opcodes.IFGT),
    LESS_OR_EQUAL(Opcodes.IFLE),
    NULL(Opcodes.IFNULL),
    NON_NULL(Opcodes.IFNONNULL)
}

fun AsmWriter.Builder.insnList(config: InsnListBuilder.() -> Unit) {
    val builder = InsnListBuilder()
    builder.config()

    this.insnListData = builder.build()
}
