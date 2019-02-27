package me.falsehonesty.asmhelper.dsl.instructions

import org.objectweb.asm.tree.InsnList

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
