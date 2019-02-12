@file:JvmName("Method")
package me.falsehonesty.asmhelper.dsl

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.writers.InjectWriter

fun inject(config: InjectWriter.Builder.() -> Unit) {
    val writer = InjectWriter.Builder()

    writer.config()

    AsmHelper.asmWriters.add(writer.build())
}
