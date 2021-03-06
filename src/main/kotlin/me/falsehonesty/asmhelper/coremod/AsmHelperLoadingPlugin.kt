package me.falsehonesty.asmhelper.coremod

//#if MC<=11202
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin

class AsmHelperLoadingPlugin : IFMLLoadingPlugin {
    override fun getASMTransformerClass(): Array<String> {
        return arrayOf("me.falsehonesty.asmhelper.example.TestClassTransformer")
    }

    override fun getModContainerClass(): String? = null

    override fun getSetupClass(): String? = null

    override fun injectData(data: MutableMap<String, Any>?) { }

    override fun getAccessTransformerClass(): String? = null
}
//#endif
