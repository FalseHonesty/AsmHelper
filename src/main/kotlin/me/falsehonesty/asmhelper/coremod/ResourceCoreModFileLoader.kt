package me.falsehonesty.asmhelper.coremod

//#if MC>=11502
//$$ import net.minecraftforge.forgespi.coremod.ICoreModFile
//$$ import java.io.File
//$$ import java.io.Reader
//$$ import java.nio.file.Files
//$$
//$$ class ResourceCoreModFileLoader(resourceStr: String) : ICoreModFile {
//$$     private val thePath = ResourceCoreModFileLoader::class.java.classLoader.getResource(resourceStr)?.let {
//$$         File(it.toURI()).toPath()
//$$     }
//$$
//$$     override fun getOwnerId() = "asmhelper"
//$$
//$$     override fun readCoreMod() = Files.newBufferedReader(thePath)
//$$
//$$     override fun getPath() = thePath
//$$
//$$     override fun getAdditionalFile(fileName: String?): Reader? {
//$$         return if (fileName != null && thePath != null) {
//$$             Files.newBufferedReader(thePath.parent.resolve(fileName))
//$$         } else null
//$$     }
//$$ }
//#endif
