package me.falsehonesty.asmhelper.dsl

inline fun InsnListBuilder.ifClause(cond: JumpCondition, config: InsnListBuilder.() -> Unit) {
    val label = makeLabel()

    jump(cond, label)

    this.config()

    placeLabel(label)
}

inline fun InsnListBuilder.createInstance(className: String, description: String, parameters: InsnListBuilder.() -> Unit = {}) {
    new(className)
    dup()

    this.parameters()

    method {
        type = InvokeType.SPECIAL
        owner = className
        name = "<init>"
        desc = description
    }
}
