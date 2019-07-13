package me.falsehonesty.asmhelper.dsl.code

import me.falsehonesty.asmhelper.dsl.code.modifiers.Modifier
import me.falsehonesty.asmhelper.printing.prettyString
import me.falsehonesty.asmhelper.printing.verbose
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList

abstract class CodeBuilder(val codeClassNode: ClassNode) {
    abstract val modifiers: List<Modifier>

    protected fun getMethodNode() = codeClassNode.methods.find { it.name == "invoke" && it.desc == "()V" }!!

    fun transformToInstructions(): InsnList {
        val instructions = getMethodNode().instructions

        verbose("Transforming code class ${codeClassNode.name}")
        verbose("Initial instruction list:")
        verbose("\n" + instructions.prettyString())
        verbose("-----------------")

        modifiers.forEach {
            verbose("Running cycle $it")
            verbose("-----------------")

            it.modify(instructions)

            verbose("-----------------")
            verbose("After cycle $it")
            verbose("\n" + instructions.prettyString())
        }

        return instructions
    }
}