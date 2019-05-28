package me.falsehonesty.asmhelper

import me.falsehonesty.asmhelper.dsl.AsmWriter
import net.minecraft.launchwrapper.Launch

object AsmHelper {
    val classReplacers = mutableMapOf<String, String>()
    val asmWriters = mutableListOf<AsmWriter>()
    val deobf = try {
        Launch.blackboard.getOrDefault("fml.deobfuscatedEnvironment", true) as Boolean
    } catch (e: Exception) {
        true
    }
}
