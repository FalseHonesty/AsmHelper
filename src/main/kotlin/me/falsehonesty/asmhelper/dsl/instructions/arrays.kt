package me.falsehonesty.asmhelper.dsl.instructions

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.InsnNode

fun InsnListBuilder.aaload() {
    insn(InsnNode(AALOAD))
}

fun InsnListBuilder.aastore() {
    insn(InsnNode(AASTORE))
}

//    fun anewarray()

//    fun baload()
//    fun bastore()

//    fun caload()
//    fun castore()

//    fun daload()
//    fun dastore()

//    fun faload()
//    fun fastore()