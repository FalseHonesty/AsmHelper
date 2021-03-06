package me.falsehonesty.asmhelper.coremod

//#if MC<=11202
import me.falsehonesty.asmhelper.AsmHelper
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin

class AsmHelperLoadingPlugin : IFMLLoadingPlugin {
    override fun getASMTransformerClass(): Array<String> {
        return AsmHelper.serviceLoader.flatMap { it.transformerClasses() }.toTypedArray()
    }

    override fun getModContainerClass(): String? = null

    override fun getSetupClass(): String? = null

    override fun injectData(data: MutableMap<String, Any>?) { }

    override fun getAccessTransformerClass(): String? = null
}
//#endif
