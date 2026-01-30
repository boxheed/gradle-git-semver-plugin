/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

public class GitSemverStatusTask extends DefaultTask {

    public static final String NAME = "gitStatus"

    private final ExecOperations execOperations

    @Inject
    public GitSemverStatusTask(ExecOperations execOperations) {
        this.execOperations = execOperations
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        return taskContainer.create(NAME, GitSemverStatusTask) {
            group = GitSemverPlugin.GROUP
            description = 'Outputs the status of the current changes'
        }
    }

    @TaskAction
    def runTask() {

        def extension = project[GitSemverPlugin.NAME]
        def context = [:]
        context.project = project
        context.extension = extension
        def changes = this.runGitStatus(context)
        if(changes.exit == 0) {
            Loggy.lifecycle("Git status: \n{}", changes.sout? changes.sout: "No Changes")
        } else {
            Loggy.lifecycle("Git status error: \n{}\n{}", changes.serr, changes.serr)
        }
    }

    def runGitStatus(Map context) {
        Loggy.debug("command: {}, dir: {}", "git status --porcelain=v1", context.project.projectDir)
        def stdout = new ByteArrayOutputStream()
        def stderr = new ByteArrayOutputStream()
        def result = execOperations.exec {
            workingDir context.project.projectDir
            commandLine 'git', 'status', '--porcelain=v1'
            standardOutput = stdout
            errorOutput = stderr
            ignoreExitValue = true
        }

        def sout = stdout.toString().trim()
        def serr = stderr.toString().trim()
        
        Loggy.debug("stdout: {}", sout)
        Loggy.debug("stderr: {}", serr)
        Loggy.debug("exit: {}", result.exitValue)

        return [exit: result.exitValue, sout: sout, serr: serr]
    }

}
