package dev.falsehonesty.asmhelper.remapping

import dev.falsehonesty.asmhelper.dsl.instructions.Descriptor

/**
 * The Deobf Remapper is used in a deobfuscated environment.
 *
 * Essentially, it maps everything right back to itself, because everything is the same!
 */
class DeobfRemapper : Remapper {
    override fun remapClassName(className: String) = className

    override fun remapMethodName(methodDescriptor: Descriptor) = methodDescriptor.name

    override fun remapFieldName(fieldDescriptor: Descriptor): String = fieldDescriptor.name

    override fun remapDesc(desc: String) = desc
}
