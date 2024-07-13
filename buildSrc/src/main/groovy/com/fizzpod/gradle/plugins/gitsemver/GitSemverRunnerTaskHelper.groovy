package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
//import groovy.json.*

public class GitSemverRunnerTaskHelper {

    static def getExecutable(def context) {
        context.os = getOs(context)
        context.arch = getArch(context)
        def binary = getBinaryFromConfig(context)
        if(!binary.exists()) {
            throw new RuntimeException("Cannot find git-semver binary on path " + binary)
        }
        context.logger.info("Using git-semver: {}", binary)
        return binary
    }

    static def run = { String command ->
        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = command.execute()
        proc.waitForProcessOutput(sout, serr)
        proc.waitFor()
        def exitValue = proc.exitValue()
//        context.logger.info("stdout: {}", sout.toString())
//        context.logger.info("stderr: {}", serr.toString())
        
        if(exitValue != 0) {
            throw new RuntimeException("An error has occured running git-semver. Exit: " + exitValue)
        }
        return [exit: exitValue, sout: sout.toString(), serr: serr.toString()]
    }

    static def runCommand(def context) {
        context.logger.info("Running {}", context.cmd)
        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = context.cmd.execute()
        proc.waitForProcessOutput(sout, serr)
        proc.waitFor()
        def exitValue = proc.exitValue()
        context.logger.info("stdout: {}", sout.toString())
        context.logger.info("stderr: {}", serr.toString())
        
        if(exitValue != 0) {
            throw new RuntimeException("An error has occured running git-semver. Exit: " + exitValue)
        }
        return sout.toString()
    }





}