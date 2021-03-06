package me.falsehonesty.asmhelper.coremod

//#if MC>=11502
//$$ import cpw.mods.modlauncher.api.IEnvironment
//$$ import cpw.mods.modlauncher.api.ITransformationService
//$$ import cpw.mods.modlauncher.api.ITransformer
//$$ import me.falsehonesty.asmhelper.BaseClassTransformer
//$$ import net.minecraftforge.coremod.CoreModEngine
//$$ import net.minecraftforge.coremod.CoreModProvider
//$$ import java.lang.reflect.Field
//$$ import java.lang.reflect.Modifier
//$$
//$$ class TransformationService : ITransformationService {
//$$     override fun name() = "asmhelper"
//$$
//$$     override fun initialize(environment: IEnvironment) {
//$$         // The CoreModEngine class has a very small list of classes that are allowed
//$$         // to be accessed inside the coremod, however we need to reach out to this
//$$         // class during the transformation, so we add it to the list via reflection
//$$         val allowedClassesField = CoreModEngine::class.java.getDeclaredField("ALLOWED_CLASSES")
//$$         allowedClassesField.isAccessible = true
//$$
//$$         // Remove final
//$$         val modifiersField = Field::class.java.getDeclaredField("modifiers")
//$$         modifiersField.isAccessible = true
//$$         modifiersField.setInt(allowedClassesField, allowedClassesField.modifiers and Modifier.FINAL.inv())
//$$
//$$         // Change value
//$$         @Suppress("UNCHECKED_CAST")
//$$         val allowedClasses = (allowedClassesField.get(null) as List<String>).toMutableList()
//$$         allowedClasses.add("me.falsehonesty.asmhelper.AsmHelper")
//$$         allowedClassesField.set(null, allowedClasses)
//$$     }
//$$
//$$     override fun beginScanning(environment: IEnvironment) {
//$$         System.getProperty("asmhelper.transformers")?.split(',')?.map(String::trim)?.forEach {
//$$             val clazz = Class.forName(it.replace('/', '.'))
//$$             if (!BaseClassTransformer::class.java.isAssignableFrom(clazz))
//$$                 throw IllegalStateException("ASM transformer $clazz does not inherit from BaseClassTransformer")
//$$
//$$             val instance = try {
//$$                 clazz.newInstance() as BaseClassTransformer
//$$             } catch (e: InstantiationException) {
//$$                 throw IllegalStateException("Unable to construct instance of $clazz", e)
//$$             }
//$$
//$$             instance.makeTransformers()
//$$         }
//$$     }
//$$
//$$     override fun onLoad(env: IEnvironment, otherServices: MutableSet<String>) { }
//$$
//$$     override fun transformers(): List<ITransformer<*>> {
//$$         val provider = CoreModProvider()
//$$         provider.addCoreMod(ResourceCoreModFileLoader("asmhelper/transformer.js"))
//$$         return provider.coreModTransformers
//$$     }
//$$ }
//#endif
