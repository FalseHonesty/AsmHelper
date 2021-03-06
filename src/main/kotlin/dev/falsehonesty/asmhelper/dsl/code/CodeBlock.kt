package dev.falsehonesty.asmhelper.dsl.code

class CodeBlock {
    /**
     * The block you provide to this function is the literal code that will be written to the target.
     */
    fun code(code: () -> Unit) {}

    /**
     * Shadows a field. The name of the variable you assign this to must match exactly the target field.
     * If the type of the field is an interface, i.e. a List, use some implementation as the generic type [T],
     * and cast it back to the interface. Ex. shadowField<ArrayList<String>>() as List<String>
     */
    inline fun <reified T> shadowField(): T = null as T

    /**
     * Shadows a local variable in the target method.
     * The name of the variable you assign this to must be in the following pattern `local\d+` where the digit
     * at the end is the index of the local variable you are shadowing.
     */
    inline fun <reified T> shadowLocal(): T = null as T

    /**
     * Shadows a method. The name of the variable you assign this to must match exactly the target method's name. If you
     * are attempting to shadow a super method, prefix the method name with `super` and capitalize the first letter
     * of the target method like so: `super.methodName` -> `superMethodName`. If you are trying to shadow a non-super
     * method that actually starts with the word "super", prefix it with an underscore. If you are trying to shadow a
     * super method where the name starts with a capitalized letter, prefix it with `super_` instead.
     */
    inline fun <reified R> shadowMethod(): () -> R = { null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1> shadowMethod(p1: P1? = null): (P1) -> R = { null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1, P2> shadowMethod(p1: P1? = null, p2: P2? = null): (P1, P2) -> R = { _, _ -> null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1, P2, P3> shadowMethod(p1: P1? = null, p2: P2? = null, p3: P3? = null): (P1, P2, P3) -> R = { _, _, _ -> null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1, P2, P3, P4> shadowMethod(p1: P1? = null, p2: P2? = null, p3: P3? = null, p4: P4? = null): (P1, P2, P3, P4) -> R = { _, _, _, _ -> null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1, P2, P3, P4, P5> shadowMethod(p1: P1? = null, p2: P2? = null, p3: P3? = null, p4: P4? = null, p5: P5? = null): (P1, P2, P3, P4, P5) -> R = { _, _, _, _, _ -> null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1, P2, P3, P4, P5, P6> shadowMethod(p1: P1? = null, p2: P2? = null, p3: P3? = null, p4: P4? = null, p5: P5? = null, p6: P6? = null): (P1, P2, P3, P4, P5, P6) -> R = { _, _, _, _, _, _ -> null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1, P2, P3, P4, P5, P6, P7> shadowMethod(p1: P1? = null, p2: P2? = null, p3: P3? = null, p4: P4? = null, p5: P5? = null, p6: P6? = null, p7: P7? = null): (P1, P2, P3, P4, P5, P6, P7) -> R = { _, _, _, _, _, _, _ -> null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1, P2, P3, P4, P5, P6, P7, P8> shadowMethod(p1: P1? = null, p2: P2? = null, p3: P3? = null, p4: P4? = null, p5: P5? = null, p6: P6? = null, p7: P7? = null, p8: P8? = null): (P1, P2, P3, P4, P5, P6, P7, P8) -> R = { _, _, _, _, _, _, _, _ -> null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1, P2, P3, P4, P5, P6, P7, P8, P9> shadowMethod(p1: P1? = null, p2: P2? = null, p3: P3? = null, p4: P4? = null, p5: P5? = null, p6: P6? = null, p7: P7? = null, p8: P8? = null, p9: P9? = null): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R = { _, _, _, _, _, _, _, _, _ -> null as R }

    /**
     * @see shadowMethod
     */
    inline fun <reified R, P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> shadowMethod(p1: P1? = null, p2: P2? = null, p3: P3? = null, p4: P4? = null, p5: P5? = null, p6: P6? = null, p7: P7? = null, p8: P8? = null, p9: P9? = null, p10: P10? = null): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R = { _, _, _, _, _, _, _, _, _, _ -> null as R }

    companion object {
        fun methodReturn() {}
        fun aReturn(obj: Any?) {}
        fun iReturn(num: Int) {}
        fun lReturn(num: Long) {}
        fun fReturn(num: Float) {}
        fun dReturn(num: Double) {}
    }
}
