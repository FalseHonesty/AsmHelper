package me.falsehonesty.asmhelper.dsl.instructions

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

class InsnListBuilder : Opcodes {
    val insnList = InsnList()

    fun aconst_null() {
        insn(InsnNode(ACONST_NULL))
    }

    fun aload(index: Int) {
        insn(VarInsnNode(ALOAD, index))
    }

    fun areturn() {
        insn(InsnNode(ARETURN))
    }

    fun arraylength() {
        insn(InsnNode(ARRAYLENGTH))
    }

    fun astore(index: Int) {
        insn(VarInsnNode(ASTORE, index))
    }

    fun athrow() {
        insn(InsnNode(ATHROW))
    }

    fun bipush(value: Int) {
        insn(IntInsnNode(BIPUSH, value))
    }

    fun checkcast(type: String) {
        insn(TypeInsnNode(CHECKCAST, type))
    }

    fun d2f() = insn(InsnNode(D2F))

    fun d2i() = insn(InsnNode(D2I))

    fun d2l() = insn(InsnNode(D2L))

    fun dadd() = insn(InsnNode(DADD))

    fun dcmpg() = insn(InsnNode(DCMPG))

    fun dcmpl() = insn(InsnNode(DCMPL))

    fun dconst_0() = insn(InsnNode(DCONST_0))

    fun dconst_1() = insn(InsnNode(DCONST_1))

    fun ddiv() = insn(InsnNode(DDIV))

    fun dload(index: Int) = insn(VarInsnNode(DLOAD, index))

    fun dmul() = insn(InsnNode(DMUL))

    fun dneg() = insn(InsnNode(DNEG))

    fun drem() = insn(InsnNode(DREM))

    fun dreturn() = insn(InsnNode(DRETURN))

    fun dstore(index: Int) = insn(VarInsnNode(DSTORE, index))

    fun dsub() = insn(InsnNode(DSUB))

    fun dup() {
        insn(InsnNode(DUP))
    }

    fun dup_x1() = insn(InsnNode(DUP_X1))

    fun dup_x2() = insn(InsnNode(DUP_X2))

    fun dup2() = insn(InsnNode(DUP2))

    fun dup2_x1() = insn(InsnNode(DUP2_X1))

    fun dup2_x2() = insn(InsnNode(DUP2_X2))

    fun f2d() = insn(InsnNode(F2D))

    fun f2i() = insn(InsnNode(F2I))

    fun f2l() = insn(InsnNode(F2L))

    fun fadd() = insn(InsnNode(FADD))

    fun fcmpg() = insn(InsnNode(FCMPG))

    fun fcmpl() = insn(InsnNode(FCMPL))

    fun fconst_0() = insn(InsnNode(FCONST_0))

    fun fconst_1() = insn(InsnNode(FCONST_1))

    fun fconst_2() = insn(InsnNode(FCONST_2))

    fun fdiv() = insn(InsnNode(FDIV))

    fun fload(index: Int) = insn(VarInsnNode(FLOAD, index))

    fun fmul() = insn(InsnNode(FMUL))

    fun fneg() = insn(InsnNode(FNEG))

    fun frem() = insn(InsnNode(FREM))

    fun freturn() = insn(InsnNode(FRETURN))

    fun fstore(index: Int) = insn(VarInsnNode(FSTORE, index))

    fun fsub() = insn(InsnNode(FSUB))

    fun iload(value: Int) {
        insn(VarInsnNode(ILOAD, value))
    }

    fun iadd() {
        insn(InsnNode(IADD))
    }

    fun ireturn() {
        insn(InsnNode(IRETURN))
    }

    fun sipush(value: Int) {
        insn(IntInsnNode(SIPUSH, value))
    }

    fun isub() {
        insn(InsnNode(ISUB))
    }

    fun instanceof(clazzName: String) = insn(TypeInsnNode(INSTANCEOF, clazzName))

    fun new(className: String) {
        insn(TypeInsnNode(NEW, className))
    }

    fun pop() = insn(InsnNode(POP))

    fun pop2() = insn(InsnNode(POP2))

    fun ldc(constant: Any) {
        insn(LdcInsnNode(constant))
    }

    fun methodReturn() {
        insn(InsnNode(RETURN))
    }

    /**
     * Creates a new label, but does not place it anywhere in the bytecode,
     * it simply gives you a reference to it.
     */
    fun makeLabel() = LabelNode()

    /**
     * Places a previously created label.
     */
    fun placeLabel(label: LabelNode) {
        insn(label)
    }

    fun jump(condition: JumpCondition, label: LabelNode) {
        insn(JumpInsnNode(condition.opcode, label))
    }

    fun insertInsns(list: InsnList) {
        insnList.add(list)
    }

    fun build(): InsnList = insnList

    internal fun insn(node: AbstractInsnNode) {
        insnList.add(node)
    }
}

enum class JumpCondition(val opcode: Int) {
    /**
     * Equivalent to IFNE and [NOT_EQUAL]
     */
    TRUE(IFNE),
    /**
     * Equivalent to IFEQ and [EQUAL]
     */
    FALSE(IFEQ),
    EQUAL(IFEQ),
    NOT_EQUAL(IFNE),
    LESS_THAN(IFLT),
    GREATER_OR_EQUAL(IFGE),
    GREATER_THAN(IFGT),
    LESS_OR_EQUAL(IFLE),
    NULL(IFNULL),
    NON_NULL(IFNONNULL),
    GOTO(Opcodes.GOTO),
    REFS_EQUAL(IF_ACMPEQ),
    REFS_NOT_EQUAL(IF_ACMPNE)
}
