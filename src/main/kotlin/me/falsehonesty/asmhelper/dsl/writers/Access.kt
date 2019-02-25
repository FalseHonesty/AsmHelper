package me.falsehonesty.asmhelper.dsl.writers

import org.objectweb.asm.Opcodes

enum class AccessType(val opcode: Int) {
    PRIVATE(Opcodes.ACC_PRIVATE),
    PUBLIC(Opcodes.ACC_PUBLIC),
    PROTECTED(Opcodes.ACC_PROTECTED),
    STATIC(Opcodes.ACC_STATIC),
    FINAL(Opcodes.ACC_FINAL),
}
