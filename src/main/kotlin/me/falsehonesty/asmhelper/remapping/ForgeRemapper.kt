package me.falsehonesty.asmhelper.remapping

import me.falsehonesty.asmhelper.dsl.instructions.Descriptor
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper

class ForgeRemapper : Remapper {
    override fun remapClassName(className: String) = className

    override fun remapMethodName(methodDescriptor: Descriptor): String =
        FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(
            methodDescriptor.owner,
            methodDescriptor.name,
            methodDescriptor.desc
        )

    override fun remapFieldName(fieldDescriptor: Descriptor): String = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(
        fieldDescriptor.owner,
        fieldDescriptor.name,
        fieldDescriptor.desc
    )
}