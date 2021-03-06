package dev.falsehonesty.asmhelper.printing

import dev.falsehonesty.asmhelper.AsmHelper
import org.apache.logging.log4j.LogManager

val logger = LogManager.getLogger("AsmHelper")!!

fun log(message: String, level: LogLevel = LogLevel.NORMAL) {
    if (level == LogLevel.NORMAL || AsmHelper.verbose) {
        logger.info(message)
    }
}

fun verbose(message: String) = log(message, LogLevel.VERBOSE)

enum class LogLevel {
    NORMAL,
    VERBOSE
}
