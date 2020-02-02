package me.falsehonesty.asmhelper.dsl.instructions

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.TypeInsnNode

fun InsnListBuilder.anewarray(className: String) {
    insn(TypeInsnNode(ANEWARRAY, className))
}

fun InsnListBuilder.aaload() = insn(InsnNode(AALOAD))
fun InsnListBuilder.aastore() = insn(InsnNode(AASTORE))

fun InsnListBuilder.baload() = insn(InsnNode(BALOAD))
fun InsnListBuilder.bastore() = insn(InsnNode(BALOAD))

fun InsnListBuilder.caload() = insn(InsnNode(BALOAD))
fun InsnListBuilder.castore() = insn(InsnNode(BALOAD))

fun InsnListBuilder.daload() = insn(InsnNode(BALOAD))
fun InsnListBuilder.dastore() = insn(InsnNode(BALOAD))

fun InsnListBuilder.faload() = insn(InsnNode(BALOAD))
fun InsnListBuilder.fastore() = insn(InsnNode(BALOAD))

fun InsnListBuilder.array(size: Int, className: String, code: ArrayBuilder.() -> Unit) {
    int(size)
    anewarray(className)

    val array = ArrayBuilder(this)
    array.code()
}

class ArrayBuilder(private val insns: InsnListBuilder) {
    private var currentIndex = 0

    fun aadd(code: InsnListBuilder.() -> Unit) = add(code, AASTORE)
    fun badd(code: InsnListBuilder.() -> Unit) = add(code, BASTORE)
    fun cadd(code: InsnListBuilder.() -> Unit) = add(code, CASTORE)
    fun dadd(code: InsnListBuilder.() -> Unit) = add(code, DASTORE)
    fun fadd(code: InsnListBuilder.() -> Unit) = add(code, FASTORE)

    private fun add(code: InsnListBuilder.() -> Unit, opcode: Int) {
        insns.dup()
        insns.int(currentIndex++)
        insns.code()
        insns.insn(InsnNode(opcode))
    }
}