package me.falsehonesty.asmhelper.java;

public class NullReturner {
    /**
     * A hacky way to convince the kotlin compiler we have a non-nullable type,
     * without actually having to construct that type.
     *
     * We will never actually call the code using this return value, so it doesn't matter.
     *
     * @param <T> the type we're SAYING we're returning.
     * @return null. ALWAYS null.
     */
    public static <T> T getNullConstant() {
        return null;
    }
}
