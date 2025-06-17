/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.apache.commons.io.FileUtils

public class Command {

    static def execute = Loggy.wrap({ Map<?,?> x ->
        x = x + Command.run(x.command)
        return x
    })

    static def runInDir = { String command, File dir -> 
        Loggy.debug("command: {}, dir: {}", command, dir)
        def soutBuilder = new StringBuilder(), serrBuilder = new StringBuilder()
        def proc = command.execute([], dir)
        proc.waitForProcessOutput(soutBuilder, serrBuilder)
        proc.waitFor()
        def exitValue = proc.exitValue()
        def sout = soutBuilder.toString()? soutBuilder.toString(): ""
        def serr = serrBuilder.toString()? serrBuilder.toString(): ""
        
        Loggy.debug("stdout: {}", sout.toString())
        Loggy.debug("stderr: {}", serr.toString())
        Loggy.debug("exit: {}", exitValue)
        
        return [exit: exitValue, sout: sout, serr: serr]
    }

    static def run = Loggy.wrap({ String command ->
        return Command.runInDir(command, FileUtils.current())
    })

}
