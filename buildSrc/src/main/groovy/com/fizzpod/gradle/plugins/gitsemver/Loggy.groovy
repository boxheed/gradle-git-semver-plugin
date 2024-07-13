package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.logging.Logging
import org.gradle.api.logging.LogLevel

import org.codehaus.groovy.reflection.ReflectionUtils

public class Loggy {

    public static log(LogLevel level, String msg, Object... params) {
        def callingClass = ReflectionUtils.getCallingClass(2)
        Logging.getLogger(callingClass).log(level, msg, *params)
    }

    public static log(LogLevel level, String msg) {
        def callingClass = ReflectionUtils.getCallingClass(2)
        Logging.getLogger(callingClass).log(level, msg)
    }

    public static info(String msg) {
        log(LogLevel.INFO, msg)
    }
    
    public static info(String msg, Object... params) {
        log(LogLevel.INFO, msg, *params)
    }

    public static lifecycle(String msg) {
        log(LogLevel.LIFECYCLE, msg)
    }

    public static lifecycle(String msg, Object... params) {
        log(LogLevel.LIFECYCLE, msg, *params)
    }

    public static debug(String msg) {
        log(LogLevel.DEBUG, msg)
    }

    public static debug(String msg, Object... params) {
        log(LogLevel.DEBUG, msg, *params)
    }

    public static error(String msg) {
        log(LogLevel.ERROR, msg)
    }

    public static error(String msg, Object... params) {
        log(LogLevel.ERROR, msg, *params)
    }

    public static warn(String msg) {
        log(LogLevel.WARN, msg)
    }

    public static warn(String msg, Object... params) {
        log(LogLevel.WARN, msg, *params)
    }

}
