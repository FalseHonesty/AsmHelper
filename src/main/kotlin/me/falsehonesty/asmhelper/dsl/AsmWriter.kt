package me.falsehonesty.asmhelper.dsl

import org.objectweb.asm.tree.ClassNode

abstract class AsmWriter(val className: String) {
    abstract fun transform(classNode: ClassNode)
}
