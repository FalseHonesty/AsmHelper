package me.falsehonesty.asmhelper.printing

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceMethodVisitor
import java.io.PrintWriter
import java.io.StringWriter

private val textifier = Textifier()
private val methodTracer = TraceMethodVisitor(textifier)

fun InsnList.prettyString(): String {
    iterator().forEach { it.accept(methodTracer) }

    return textifierToString()
}

fun AbstractInsnNode.prettyString(): String {
    this.accept(methodTracer)

    return textifierToString().trim()
}

private fun textifierToString(): String {
    val stringWriter = StringWriter()
    textifier.print(PrintWriter(stringWriter))
    textifier.getText().clear()

    return stringWriter.toString()
}