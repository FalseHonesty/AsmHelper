package dev.falsehonesty.asmhelper.remapping

import dev.falsehonesty.asmhelper.dsl.instructions.Descriptor

interface Remapper {
    /**
     * Remaps a class name.
     *
     * Deobf class name --> obf class name.
     */
    fun remapClassName(className: String): String

    /**
     * Remaps a method name.
     *
     * Deobf method owner, name & desc --> obf method name
     */
    fun remapMethodName(owner: String, methodName: String, methodDesc: String) = remapMethodName(Descriptor(owner, methodName, methodDesc))

    /**
     * Remaps a method name.
     *
     * Deobf method descriptor --> obf method name
     */
    fun remapMethodName(methodDescriptor: Descriptor): String

    /**
     * Remaps a field name.
     *
     * Deobf field owner & name --> obf field name
     */
    fun remapFieldName(owner: String, fieldName: String, fieldDesc: String) = remapFieldName(Descriptor(owner, fieldName, fieldDesc))

    /**
     * Remaps a field name.
     *
     * Deobf field descriptor --> obf field name
     */
    fun remapFieldName(fieldDescriptor: Descriptor): String

    fun remapDesc(desc: String): String

    /**
     * Maps an invocation instruction's name.
     */
    fun mapInvocation(methodName: String): String = methodName

    /**
     * Maps a field instruction's name.
     */
    fun mapFieldAccess(fieldName: String): String = fieldName
}
