package me.falsehonesty.asmhelper.dsl.instructions

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode

/**
 * An abstraction over iconst, bipush, sipush, and ldc, picking the best one
 * available.
 */
fun InsnListBuilder.int(number: Int) {
    when (number) {
        -1 -> insn(InsnNode(Opcodes.ICONST_M1))
        0 -> insn(InsnNode(Opcodes.ICONST_0))
        1 -> insn(InsnNode(Opcodes.ICONST_1))
        2 -> insn(InsnNode(Opcodes.ICONST_2))
        3 -> insn(InsnNode(Opcodes.ICONST_3))
        4 -> insn(InsnNode(Opcodes.ICONST_4))
        5 -> insn(InsnNode(Opcodes.ICONST_5))
        in 6..127 -> bipush(number)
        in -127..-2 -> bipush(number)
        in 128..32768 -> sipush(number)
        in -32768..-128 -> sipush(number)
        else -> ldc(number)
    }
}

/**
 * An abstraction over fconst and ldc, picking the best one
 * available.
 */
fun InsnListBuilder.double(number: Double) {
    when (number) {
        0.0 -> dconst_0()
        1.0 -> dconst_1()
        else -> ldc(number)
    }
}

/**
 * An abstraction over fconst and ldc, picking the best one
 * available.
 */
fun InsnListBuilder.float(number: Float) {
    when (number) {
        0f -> fconst_0()
        1f -> fconst_1()
        2f -> fconst_2()
        else -> ldc(number)
    }
}

inline fun InsnListBuilder.ifClause(cond: JumpCondition, code: InsnListBuilder.() -> Unit) {
    val label = makeLabel()

    jump(cond, label)

    this.code()

    placeLabel(label)
}

inline fun InsnListBuilder.createInstance(className: String, constructorDescription: String, parameters: InsnListBuilder.() -> Unit = {}) {
    new(className)
    dup()

    this.parameters()

    invoke(
        InvokeType.SPECIAL,
        className,
        "<init>",
        constructorDescription
    )
}

inline fun InsnListBuilder.ifElseClause(cond: JumpCondition, builder: IfElseBuilder.() -> Unit) {
    val ifElse = IfElseBuilder()

    ifElse.builder()

    val ifLabel = makeLabel()
    val endLabel = makeLabel()

    jump(cond, ifLabel)

    insertInsns(ifElse.elseCode)

    jump(JumpCondition.GOTO, endLabel)

    placeLabel(ifLabel)

    insertInsns(ifElse.ifCode)

    placeLabel(endLabel)
}

class IfElseBuilder {
    var ifCode = InsnList()
    var elseCode = InsnList()

    fun ifCode(builder: InsnListBuilder.() -> Unit) {
        val insn = InsnListBuilder()

        insn.builder()

        ifCode = insn.build()
    }

    fun elseCode(builder: InsnListBuilder.() -> Unit) {
        val insn = InsnListBuilder()

        insn.builder()

        elseCode = insn.build()
    }
}
