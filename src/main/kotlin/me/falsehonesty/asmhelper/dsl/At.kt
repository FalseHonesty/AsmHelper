package me.falsehonesty.asmhelper.dsl

data class At(val value: InjectionPoint, val before: Boolean = true, val shift: Int = 0)

enum class InjectionPoint {
    HEAD,
    TAIL
}
