package dev.falsehonesty.asmhelper.dsl.writers

import dev.falsehonesty.asmhelper.AsmHelper
import dev.falsehonesty.asmhelper.dsl.AsmWriter
import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.code.CodeBlock
import dev.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import dev.falsehonesty.asmhelper.printing.logger
import dev.falsehonesty.asmhelper.printing.prettyString
import dev.falsehonesty.asmhelper.printing.verbose
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode

class InjectWriter(
    className: String,
    private val methodName: String,
    private val methodDesc: String,
    private val at: At,
    private val insnListBuilder: (InsnListBuilder.() -> Unit)?,
    private val codeBlockClassName: String?,
    private val fieldMaps: Map<String, String>,
    private val methodMaps: Map<String, String>
) : AsmWriter(className) {
    override fun transform(classNode: ClassNode) {
        AsmHelper.fieldMaps = fieldMaps
        AsmHelper.methodMaps = methodMaps

        classNode.methods
            .find {
                val remapped = AsmHelper.remapper.remapMethodName(classNode.name, it.name, it.desc)
                val remappedDesc = AsmHelper.remapper.remapDesc(it.desc)

                remappedDesc == methodDesc && (remapped == methodName || methodMaps[remapped] == methodName)
            }
            ?.let { injectInsnList(it, classNode) } ?: logger.error("No methods found for target $methodName")
    }

    private fun injectInsnList(method: MethodNode, classNode: ClassNode) {
        val nodes = at.getTargetedNodes(method)

        val instructions = transformToInstructions(insnListBuilder, codeBlockClassName, method, classNode) ?: return

        if (nodes.isEmpty()) {
            logger.error("Couldn't find any matching nodes for $this")
        } else verbose("$this matched the following ${nodes.size} targets")

        nodes.forEachIndexed { i, node ->
            verbose("$i.    ${node.prettyString()}")
            insertToNode(method, node, instructions)
        }
    }

    private fun insertToNode(method: MethodNode, node: AbstractInsnNode, insnList: InsnList) {
        var target = node

        if (at.shift < 0) {
            repeat(-at.shift) {
                target = target.previous
            }
        } else if (at.shift > 0) {
            repeat(at.shift) {
                target = target.next
            }
        }

        if (at.before) {
            method.instructions.insertBefore(target, insnList)
        } else {
            method.instructions.insert(target, insnList)
        }
    }

    override fun toString(): String {
        return "InjectWriter{className=$className, methodName=$methodName, methodDesc=$methodDesc, at=$at}"
    }

    class Builder {
        lateinit var className: String
        lateinit var methodName: String
        lateinit var methodDesc: String
        lateinit var at: At
        private var insnListBuilder: (InsnListBuilder.() -> Unit)? = null
        private var codeBlockClassName: String? = null
        var fieldMaps = mapOf<String, String>()
        var methodMaps = mapOf<String, String>()

        @Throws(IllegalStateException::class)
        fun build(): AsmWriter {
            return InjectWriter(
                className, methodName, methodDesc,
                at, insnListBuilder, codeBlockClassName,
                fieldMaps, methodMaps
            )
        }

        fun insnList(config: InsnListBuilder.() -> Unit) {
            this.insnListBuilder = config
        }

        fun codeBlock(code: CodeBlock.() -> Unit) {
            this.codeBlockClassName = code.javaClass.name + "$1"
        }
    }
}

fun asm(bytecode: InsnListBuilder.() -> Unit) {}
