/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import java.io.File
import org.gradle.api.Project
import org.gradle.process.ExecOperations

public class Command {

    /**
     * Executes a command using ExecOperations.
     * @param execOperations the Gradle ExecOperations service
     * @param x a map containing 'commandParts' (List<String>) and 'projectDir' (File or String)
     * @return the map updated with 'exit', 'sout', and 'serr'
     */
    static Map execute(ExecOperations execOperations, Map x) {
        def dir = x.projectDir
        if (dir == null) {
            dir = new File(".")
        } else if (!(dir instanceof File)) {
            dir = new File(dir.toString())
        }
        def result = Command.runInDir(execOperations, x.commandParts, dir)
        return x + result
    }

    /**
     * Legacy execute method for when ExecOperations is not available (e.g. in some tests).
     * @deprecated use execute(ExecOperations, Map)
     */
    @Deprecated
    static Map execute(Map x) {
        def dir = x.projectDir
        if (dir == null) {
            dir = new File(".")
        } else if (!(dir instanceof File)) {
            dir = new File(dir.toString())
        }
        def command = x.commandParts ? x.commandParts.join(" ") : x.command
        def result = Command.runInDir(command, dir)
        return x + result
    }

    /**
     * Runs a command in a specific directory using ExecOperations.
     */
    static Map runInDir(ExecOperations execOperations, List<String> commandParts, File dir) { 
        Loggy.debug("command: {}, dir: {}", commandParts, dir)
        def stdout = new ByteArrayOutputStream()
        def stderr = new ByteArrayOutputStream()
        
        def result = execOperations.exec {
            it.workingDir dir
            it.commandLine commandParts
            it.standardOutput = stdout
            it.errorOutput = stderr
            it.ignoreExitValue = true
        }
        
        def exitValue = result.exitValue
        def sout = stdout.toString()
        def serr = stderr.toString()
        
        Loggy.debug("stdout: {}", sout)
        Loggy.debug("stderr: {}", serr)
        Loggy.debug("exit: {}", exitValue)
        
        return [exit: exitValue, sout: sout, serr: serr]
    }

    /**
     * Legacy runInDir method using Process.execute().
     * @deprecated use runInDir(ExecOperations, List, File)
     */
    @Deprecated
    static Map runInDir(String command, File dir) { 
        Loggy.debug("command: {}, dir: {}", command, dir)
        def soutBuilder = new StringBuilder(), serrBuilder = new StringBuilder()
        def proc = command.execute([], dir)
        proc.waitForProcessOutput(soutBuilder, serrBuilder)
        proc.waitFor()
        def exitValue = proc.exitValue()
        def sout = soutBuilder.toString()? soutBuilder.toString(): ""
        def serr = serrBuilder.toString()? serrBuilder.toString(): ""
        
        Loggy.debug("stdout: {}", sout)
        Loggy.debug("stderr: {}", serr)
        Loggy.debug("exit: {}", exitValue)
        
        return [exit: exitValue, sout: sout, serr: serr]
    }

}
