package me.falsehonesty.asmhelper.dsl.instructions

fun InsnListBuilder.astore(): Local {
    astore(currentLocalIndex)

    return Local(currentLocalIndex++, LocalType.OBJECT)
}

fun InsnListBuilder.fstore(): Local {
    fstore(currentLocalIndex)

    return Local(currentLocalIndex++, LocalType.FLOAT)
}

fun InsnListBuilder.istore(): Local {
    istore(currentLocalIndex)

    return Local(currentLocalIndex++, LocalType.INT)
}

fun InsnListBuilder.dstore(): Local {
    dstore(currentLocalIndex)

    return Local(currentLocalIndex++, LocalType.DOUBLE)
}

fun InsnListBuilder.lstore(): Local {
    lstore(currentLocalIndex)

    return Local(currentLocalIndex++, LocalType.LONG)
}

fun InsnListBuilder.load(local: Local) {
    when (local.type) {
        LocalType.OBJECT -> aload(local.index)
        LocalType.FLOAT -> fload(local.index)
        LocalType.INT -> iload(local.index)
        LocalType.DOUBLE -> dload(local.index)
        LocalType.LONG -> lload(local.index)
    }
}

data class Local(val index: Int, val type: LocalType)

enum class LocalType {
    OBJECT,
    FLOAT,
    INT,
    DOUBLE,
    LONG
}