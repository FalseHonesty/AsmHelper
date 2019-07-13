package me.falsehonesty.asmhelper.dsl.writers

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.AsmHelper.logger
import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.dsl.At
import me.falsehonesty.asmhelper.dsl.code.CodeBlock
import me.falsehonesty.asmhelper.dsl.code.InjectCodeBuilder
import me.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import org.objectweb.asm.ClassReader
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

// <<<<<<< HEAD
        val instructions = when {
            insnListBuilder != null && codeBlockClassName != null -> {
                logger.error("$this specifies both an insnList and a codeBlock, please pick one or the other.")
                return
            }
            insnListBuilder != null -> {
                val builder = InsnListBuilder(method)
                insnListBuilder.let { builder.it() }
                builder.build()
            }
            codeBlockClassName != null -> {
                val clazzPath = codeBlockClassName.replace('.', '/') + ".class"
                val clazzInputStream = javaClass.classLoader.getResourceAsStream(clazzPath)

                val clazzReader = ClassReader(clazzInputStream)
                val codeClassNode = ClassNode()
                clazzReader.accept(codeClassNode, ClassReader.SKIP_FRAMES)

                val codeBuilder = InjectCodeBuilder(codeClassNode, classNode, method)

                codeBuilder.transformToInstructions()
            }
            else -> {
                logger.error("$this does not have instructions to inject. You must specify an insnList or codeBlock.")
                return
            }
        }

        if (nodes.isEmpty())
            logger.error("Couldn't find any matching nodes for $this")

        nodes.forEach { insertToNode(method, it, instructions) }
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
