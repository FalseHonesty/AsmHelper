package me.falsehonesty.asmhelper.remapping

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.instructions.Descriptor
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper

class ForgeRemapper : Remapper {
    override fun remapClassName(className: String): String {
        val mapped = FMLDeobfuscatingRemapper.INSTANCE.map(className)
        val unmapped = FMLDeobfuscatingRemapper.INSTANCE.unmap(className)

        return mapped
    }

    override fun remapMethodName(methodDescriptor: Descriptor): String = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(
        methodDescriptor.owner,
        methodDescriptor.name,
        methodDescriptor.desc
    )

    override fun remapFieldName(fieldDescriptor: Descriptor): String = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(
        fieldDescriptor.owner,
        fieldDescriptor.name,
        fieldDescriptor.desc
    )

    override fun mapInvocation(methodName: String): String {
        return AsmHelper.methodMaps.getOrDefault(methodName, methodName)
    }

    override fun mapFieldAccess(fieldName: String): String {
        return AsmHelper.fieldMaps.getOrDefault(fieldName, fieldName)
    }

    override fun remapDesc(desc: String): String {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc)
    }
}