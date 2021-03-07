package dev.falsehonesty.asmhelper.dsl

import org.objectweb.asm.tree.ClassNode

abstract class AsmWriter(val className: String) {
    abstract fun transform(classNode: ClassNode)

    abstract class AsmWriterBuilder {
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
    }
}
