package me.falsehonesty.asmhelper.dsl.instructions

inline fun InsnListBuilder.ifClause(cond: JumpCondition, config: InsnListBuilder.() -> Unit) {
    val label = makeLabel()

    jump(cond, label)

    this.config()

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
