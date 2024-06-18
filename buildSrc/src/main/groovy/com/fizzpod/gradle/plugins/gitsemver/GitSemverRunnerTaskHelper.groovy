package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import groovy.json.*
import javax.inject.Inject
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.io.FileUtils
import org.kohsuke.github.*
import groovy.json.JsonSlurper
import com.jayway.jsonpath.*
import us.springett.cvss.*

import static com.fizzpod.gradle.plugins.gitsemver.GitSemverInstallHelper.*

public class GitSemverRunnerTaskHelper {

    private JsonSlurper jsonSlurper = new JsonSlurper()


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