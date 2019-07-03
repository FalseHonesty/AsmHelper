package me.falsehonesty.asmhelper.dsl.code

import org.objectweb.asm.tree.ClassNode

abstract class CodeBuilder(val classNode: ClassNode) {
    protected fun getMethodNode() = classNode.methods.find { it.name == "invoke" && it.desc == "()V" }!!
}