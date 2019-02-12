@file:JvmName("Method")
package me.falsehonesty.asmhelper.dsl

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.writers.InjectWriter
import me.falsehonesty.asmhelper.dsl.writers.OverwriteWriter

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

fun overwrite(config: OverwriteWriter.Builder.() -> Unit) {
    val writer = OverwriteWriter.Builder()

    writer.config()

    AsmHelper.asmWriters.add(writer.build())
}
