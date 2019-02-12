@file:JvmName("Method")
package me.falsehonesty.asmhelper.dsl

import me.falsehonesty.asmhelper.AsmHelper

fun inject(config: AsmWriter.Builder.() -> Unit) {
    val writer = AsmWriter.Builder(MethodType.INJECT)

    writer.config()

    AsmHelper.asmWriters.add(writer.build())
}

enum class MethodType {
    INJECT
}
