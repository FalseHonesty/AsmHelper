@file:JvmName("Method")
package me.falsehonesty.asmhelper.dsl

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.writers.FieldWriter
import me.falsehonesty.asmhelper.dsl.writers.InjectWriter
import me.falsehonesty.asmhelper.dsl.writers.OverwriteWriter
import me.falsehonesty.asmhelper.dsl.writers.RemoveWriter

/**
 * Injects instructions into the specified place.
 *
 * This is a purely additive action and will not remove any existing bytecode.
 */
fun inject(config: InjectWriter.Builder.() -> Unit) {
    val writer = InjectWriter.Builder()

    writer.config()

    AsmHelper.asmWriters.add(writer.build())
}

/**
 * Removes all existing instructions and replaces them with the specified bytecode.
 *
 * This IS a destructive action.
 */
fun overwrite(config: OverwriteWriter.Builder.() -> Unit) {
    val writer = OverwriteWriter.Builder()

    writer.config()

    AsmHelper.asmWriters.add(writer.build())
}

/**
 * Adds the specified field into the transformed class.
 *
 * This instruction will not harm any existing code.
 */
fun applyField(config: FieldWriter.Builder.() -> Unit) {
    val writer = FieldWriter.Builder()

    writer.config()

    AsmHelper.asmWriters.add(writer.build())
}

/**
 * Removes a specified number of instructions in a method.
 *
 * This IS a destructive operation (obviously).
 */
fun remove(config: RemoveWriter.Builder.() -> Unit) {
    val writer = RemoveWriter.Builder()

    writer.config()

    AsmHelper.asmWriters.add(writer.build())
}
