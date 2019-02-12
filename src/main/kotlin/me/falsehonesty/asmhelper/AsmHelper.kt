package me.falsehonesty.asmhelper

import me.falsehonesty.asmhelper.dsl.AsmWriter
import net.minecraft.launchwrapper.Launch

object AsmHelper {
    val classReplacers = mutableMapOf<String, String>()
    val asmWriters = mutableListOf<AsmWriter>()
    val obfuscated = Launch.blackboard.getOrDefault("fml.deobfuscatedEnvironment", false) as Boolean
}
