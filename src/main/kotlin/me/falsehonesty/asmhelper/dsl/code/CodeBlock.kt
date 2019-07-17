package me.falsehonesty.asmhelper.dsl.code

class CodeBlock {
    fun code(code: () -> Unit) {}

    /**
     * Shadows a field.
     * If the type of the field is an interface, i.e. a List, use some implementation as the generic type [T],
     * and cast it back to the interface. Ex. shadowField<ArrayList<String>>() as List<String>
     */
    inline fun <reified T> shadowField(): T = null as T

    inline fun <reified T> shadowLocal(): T = null as T

    inline fun <reified R> shadowMethod(): () -> R = { null as R }

    inline fun <reified R, P1> shadowMethod(p1: P1? = null): (P1) -> R = { null as R }

    inline fun <reified R, P1, P2> shadowMethod(p1: P1? = null, p2: P2? = null): (P1, P2) -> R = { _, _ -> null as R }

    inline fun <reified R, P1, P2, P3> shadowMethod(p1: P1? = null, p2: P2? = null, p3: P3? = null): (P1, P2, P3) -> R = { _, _, _ -> null as R }
}