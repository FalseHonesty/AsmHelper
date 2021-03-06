package me.falsehonesty.asmhelper.remapping

import me.falsehonesty.asmhelper.AsmHelper
import me.falsehonesty.asmhelper.dsl.instructions.Descriptor

//#if MC<=11202
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
//#else
//$$ import net.minecraftforge.fml.common.ObfuscationReflectionHelper
//$$ import cpw.mods.modlauncher.api.INameMappingService
//$$ import org.objectweb.asm.Type
//#endif

class ForgeRemapper : Remapper {
    override fun remapClassName(className: String): String {
        //#if MC<=11202
        return FMLDeobfuscatingRemapper.INSTANCE.map(className)
        //#else
        //$$ return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.CLASS, className)
        //#endif
    }

    override fun remapMethodName(methodDescriptor: Descriptor): String {
        //#if MC<=11202
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(
            methodDescriptor.owner,
            methodDescriptor.name,
            methodDescriptor.desc
        )
        //#else
        //$$ return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, methodDescriptor.name)
        //#endif
    }

    override fun remapFieldName(fieldDescriptor: Descriptor): String {
        //#if MC<=11202
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(
            fieldDescriptor.owner,
            fieldDescriptor.name,
            fieldDescriptor.desc
        )
        //#else
        //$$ return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.CLASS, fieldDescriptor.name)
        //#endif
    }

    override fun mapInvocation(methodName: String): String {
        return AsmHelper.methodMaps.getOrDefault(methodName, methodName)
    }

    override fun mapFieldAccess(fieldName: String): String {
        return AsmHelper.fieldMaps.getOrDefault(fieldName, fieldName)
    }

    override fun remapDesc(desc: String): String {
        //#if MC<=11202
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc)
        //#else
        //$$ val type = Type.getMethodType(desc)
        //$$ return buildString {
        //$$     append('(')
        //$$     for (arg in type.argumentTypes) {
        //$$         append(remapClassName(arg.descriptor))
        //$$     }
        //$$     append(')')
        //$$     append(remapClassName(type.returnType.descriptor))
        //$$ }
        //#endif
    }
}
