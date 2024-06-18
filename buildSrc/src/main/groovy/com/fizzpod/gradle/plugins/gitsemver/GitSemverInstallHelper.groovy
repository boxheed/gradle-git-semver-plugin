package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import groovy.json.*
import javax.inject.Inject
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.io.FileUtils
import org.kohsuke.github.*

public class GitSemverInstallHelper {

    public static final String WINDOWS = "windows"
    public static final String LINUX = "linux"
    public static final String MAC = "darwin"
    
    public static final String AMD64 = "amd64"
    public static final String ARM64 = "arm64"

    public static final String GITSEMVER_INSTALL_DIR = ".git-semver"


    static def getInstallRoot(def context) {
        def root = context.project.rootDir
        return new File(root, GITSEMVER_INSTALL_DIR)
    }

    static def getBinaryArchiveName(def version, def os, def arch) {
        getBinaryName(version, os, arch) + ".tar.gz"
    }

    static def getBinaryName(def version, def os, def arch) {
        def name = "git-semver_" + version + "_" + os + "_" + arch
        if(os.equals(WINDOWS)) {
            name = name + ".exe"
        }
        return name
    }
    
    static def getBinaryFile(def context) {
        return new File(getInstallRoot(context), getBinaryName(context.release.getName(), context.os, context.arch))
    }

    static def getBinaryFromConfig(def context) {
        def binary = context.extension.binary
        if(binary != null && !"".equals(binary.trim())) {
            return new File(binary)
        }
        return getBinaryFile(context)
    }

    static def getOs(def context) {
        def os = null
        if(context.extension.os != null) {
            os = context.extension.os
        } else  if(SystemUtils.IS_OS_WINDOWS) {
            os = WINDOWS
        } else if(SystemUtils.IS_OS_MAC) {
            os = MAC
        } else if(SystemUtils.IS_OS_LINUX) {
            os = LINUX
        }
        if(os == null) {
            throw new RuntimeException("Unsupported operating system for git-semver: " + SystemUtils.OS_NAME)
        }
        context.logger.info("OS resolved to {}", os);
        return os;
    }

    static def getArch(def context) {
        def systemArch = SystemUtils.OS_ARCH
        //Assume ARM
        def arch = ARM64
        if(context.extension.arch != null) {
            arch = context.extension.arch
        } else if(systemArch.equalsIgnoreCase("x86_64") || systemArch.equalsIgnoreCase("amd64")) {
            arch = AMD64
        } 
        context.logger.info("Architecture resolved to {}", arch)
        return arch
    }
    

}