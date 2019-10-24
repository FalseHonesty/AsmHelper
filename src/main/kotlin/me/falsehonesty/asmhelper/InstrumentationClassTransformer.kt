package me.falsehonesty.asmhelper

import org.apache.logging.log4j.LogManager
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain

abstract class InstrumentationClassTransformer : ClassFileTransformer {
    private val logger = LogManager.getLogger("AsmHelper")
    private var calledSetup = false

    private fun setup() {
        makeTransformers()
    }

    /**
     * This is where you would place all of your asm helper dsl magic
     *
     */
    abstract fun makeTransformers()

    override fun transform(
        classLoader: ClassLoader?,
        className: String?,
        p2: Class<*>?,
        p3: ProtectionDomain?,
        basicClass: ByteArray?
    ): ByteArray? {
        if (basicClass == null || className == null) return null

        if (className.startsWith("kotlin.") || className.startsWith("me.falsehonesty.asmhelper.") || className.startsWith(
                this.javaClass.name
            )
        ) {
            return basicClass
        }

        if (!calledSetup) {
            setup()
            calledSetup = true
        }

        AsmHelper.classReplacers[className]?.let { classFile ->
            logger.info("Completely replacing {} with data from {}.", className, classFile)

            return loadClassResource(classFile)
        }

        val writers = AsmHelper.asmWriters
            .filter { it.className.replace('/', '.') == className }
            .ifEmpty { return basicClass }

        logger.info("Transforming class {}", className)

        val classReader = ClassReader(basicClass)
        val classNode = ClassNode()
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES)

        writers.forEach {
            logger.info("Applying AsmWriter {} to class {}", it, className)

            it.transform(classNode)
        }

        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
        try {
            classNode.accept(classWriter)
        } catch (e: Throwable) {
            logger.error("Exception when transforming {} : {}", className, e.javaClass.simpleName)
            e.printStackTrace()
        }


        return classWriter.toByteArray()
    }

    private fun loadClassResource(name: String): ByteArray {
        return this::class.java.classLoader?.getResourceAsStream(name)!!.readBytes()
    }
}