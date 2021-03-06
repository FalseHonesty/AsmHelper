package me.falsehonesty.asmhelper

import me.falsehonesty.asmhelper.dsl.AsmWriter
import me.falsehonesty.asmhelper.printing.log
import me.falsehonesty.asmhelper.remapping.DeobfRemapper
import me.falsehonesty.asmhelper.remapping.ForgeRemapper
import me.falsehonesty.asmhelper.remapping.NotchRemapper
import me.falsehonesty.asmhelper.remapping.Remapper
import java.lang.Exception
import java.util.ServiceLoader

//#if MC<=11202
import net.minecraft.launchwrapper.Launch

//#else
//$$ import cpw.mods.modlauncher.Launcher
//$$ import net.minecraftforge.fml.loading.FMLClientLaunchProvider
//$$ import net.minecraftforge.fml.loading.FMLCommonLaunchHandler
//$$ import net.minecraftforge.fml.loading.FMLLoader
//$$ import net.minecraftforge.fml.loading.FMLServerLaunchProvider
//$$ import org.objectweb.asm.tree.ClassNode
//#endif

object AsmHelper {
    val classReplacers = mutableMapOf<String, String>()
    val asmWriters = mutableListOf<AsmWriter>()

    val remapper: Remapper
    var verbose = System.getProperty("asmhelper.verbose", "false").toBoolean()

    internal var fieldMaps = mapOf<String, String>()
    internal var methodMaps = mapOf<String, String>()

    internal val serviceLoader = ServiceLoader.load(ClassTransformationService::class.java)

    init {
        val fmlDeobf = try {
            //#if MC<=11202
            Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean
            //#elseif MC==11502
            //$$ val commonLaunchHandlerField = FMLLoader::class.java.getDeclaredField("commonLaunchHandler")
            //$$ commonLaunchHandlerField.isAccessible = true
            //$$
            //$$ (commonLaunchHandlerField.get(null) as FMLCommonLaunchHandler).let {
            //$$     it is FMLClientLaunchProvider || it is FMLServerLaunchProvider
            //$$ }
            //#else
            //$$ FMLLoader.isProduction()
            //#endif
        } catch (e: Exception) {
            null
        }

        remapper = when {
            fmlDeobf == true -> DeobfRemapper()
            fmlDeobf == false -> ForgeRemapper()
            System.getProperty("asmhelper.deobf", "false")!!.toBoolean() -> DeobfRemapper()
            else -> NotchRemapper()
        }

        log("Selected the $remapper remapper")
    }

    //#if MC>=11502
    //$$ @JvmStatic
    //$$ fun classNames() = asmWriters.map { it.className }
    //$$
    //$$ @JvmStatic
    //$$ fun transform(classNode: ClassNode) {
    //$$     log("Transforming ${classNode.name}")
    //$$     asmWriters.find {
    //$$         it.className.replace('.', '/') == classNode.name
    //$$     }?.also {
    //$$         it.transform(classNode)
    //$$     }
    //$$ }
    //#endif
}
