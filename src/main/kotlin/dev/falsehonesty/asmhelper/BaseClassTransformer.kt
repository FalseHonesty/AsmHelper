package dev.falsehonesty.asmhelper

//#if MC<=11202
import dev.falsehonesty.asmhelper.printing.log
import dev.falsehonesty.asmhelper.printing.logger
import net.minecraft.launchwrapper.IClassTransformer
import net.minecraft.launchwrapper.LaunchClassLoader
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

abstract class BaseClassTransformer : IClassTransformer {
//#else
//$$ abstract class BaseClassTransformer {
//#endif
    val mcVersion =
        //#if MC==10809
        10809
        //#elseif MC==11202
        //$$ 11202
        //#elseif MC==11502
        //$$ 11502
        //#else
        //$$ 11605
        //#endif

    /**
     * This is where you would place all of your asm helper dsl magic
     *
     */
    abstract fun makeTransformers()

//#if MC<=11202
    private var calledSetup = false

    private fun setup() {
        val classLoader = this.javaClass.classLoader as LaunchClassLoader

        classLoader.addTransformerExclusion("kotlin.")
        classLoader.addTransformerExclusion("dev.falsehonesty.asmhelper.")
        classLoader.addTransformerExclusion("org.objenesis.")
        classLoader.addTransformerExclusion(this.javaClass.name)

        setup(classLoader)

        makeTransformers()
    }

    /**
     * Enables debug class loading. This means all transformed classes will be printed.
     */
    protected fun debugClassLoading() {
        System.setProperty("legacy.debugClassLoading", "true")
        System.setProperty("legacy.debugClassLoadingSave", "true")
    }

    protected open fun setup(classLoader: LaunchClassLoader) {}

    override fun transform(name: String?, transformedName: String?, basicClass: ByteArray?): ByteArray? {
        if (basicClass == null) return null

        if (!calledSetup) {
            setup()
            calledSetup = true
        }

        AsmHelper.classReplacers[transformedName]?.let { classFile ->
            log("Completely replacing $transformedName with data from $classFile.")

            return loadClassResource(classFile)
        }

        val writers = AsmHelper.asmWriters
            .filter { it.className.replace('/', '.') == transformedName }
            .ifEmpty { return basicClass }

        log("Transforming class $transformedName")

        val classReader = ClassReader(basicClass)
        val classNode = ClassNode()
        classReader.accept(classNode, ClassReader.SKIP_FRAMES)

        // In case we want our classes to support indy & other newer class file features.
        classNode.version = Opcodes.V1_8

        writers.forEach {
            log("Applying AsmWriter $it to class $transformedName")

            it.transform(classNode)
        }

        val classWriter = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
        try {
            classNode.accept(classWriter)
        } catch (e: Throwable) {
            logger.error("Exception when transforming $transformedName : ${e.javaClass.simpleName}", e)
        }

        return classWriter.toByteArray()
    }

    private fun loadClassResource(name: String): ByteArray {
        return this::class.java.classLoader.getResourceAsStream(name).readBytes()
    }
//#endif
}
