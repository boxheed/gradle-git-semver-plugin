package com.fizzpod.gradle.plugins.gitsemver

import org.apache.commons.lang3.SystemUtils

public class OS {

    enum Family {
        WINDOWS("windows"),
        LINUX("linux"),
        MAC("darwin")

        final String id

        Family(String id) {
            this.id = id
        }

        static Family findByName(String name) {
            def res = values().find { 
                it.toString().equalsIgnoreCase(name?.trim()) || it.id.equalsIgnoreCase(name?.trim())
            }
            println(res)
            return res
        }

        static Family resolve() {
            def family = OS.Family.WINDOWS
            if(SystemUtils.IS_OS_MAC) {
                family = OS.Family.MAC
            } else if(SystemUtils.IS_OS_LINUX) {
                family = OS.Family.LINUX
            }
            return family
        }

    }

    enum Arch {
        AMD64("amd64"),
        ARM64("arm64")

        final String id

        Arch(String id) {
            this.id = id
        }

        static Arch findByName(String name) {
            return values().find { 
                it.toString().equalsIgnoreCase(name?.trim()) || it.id.equalsIgnoreCase(name?.trim())
            }
        }

        static Arch resolve() {
            def systemArch = SystemUtils.OS_ARCH
            def arch = OS.Arch.ARM64
            if(systemArch.equalsIgnoreCase("x86_64") || systemArch.equalsIgnoreCase("amd64")) {
                arch = OS.Arch.AMD64
            } 
            return arch
        }
    }

    static def getOs = { String name ->
        def os = null
        if(!name || name == "current") {
            os = OS.Family.resolve()
        } else {
            os = OS.Family.findByName(name)
        } 
        //Loggy.info("OS resolved to {}", os)
        return os;
    }.memoize()

    static def getArch = { String name ->
        def arch = null
        if(!name || name == "current") {
            arch = OS.Arch.resolve()
        } else {
            arch = OS.Arch.findByName(name)
        } 
        //Loggy.info("Architecture resolved to {}", arch)
        return arch
    }.memoize()

}