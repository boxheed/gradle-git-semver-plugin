/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import groovy.json.*
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

public class GitSemverCurrentVersionTask extends DefaultTask {

    public static final String NAME = "semver"

    @Inject
    public GitSemverCurrentVersionTask() {
    }

    static register(Project project) {
        Loggy.debug("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        return taskContainer.create([name: NAME,
            type: GitSemverCurrentVersionTask,
            dependsOn: [],
            group: GitSemverPlugin.GROUP,
            description: 'Gets the current semantic version'])
    }

    @TaskAction
    def runTask() {
        def extension = project.extensions.getByName(GitSemverPlugin.NAME)
        def context = [:]
        
        // For InstallTask
        context.projectDir = project.rootDir
        context.semverDir = extension.location.get()
        context.repository = extension.repository.get()
        context.version = extension.version.get()
        context.os = extension.os.getOrNull()
        context.arch = extension.arch.getOrNull()
        context.ttl = extension.ttl.get()
        
        // For command
        context.stable = extension.stable.get()

        def version = GitSemverCurrentVersionTask.run(context)
        project.logger.lifecycle(version)
    }

    static def run = { context ->
        context.mode = "latest"
        def res = Optional.ofNullable(context)
            .map(x -> GitSemverInstallTask.location(x))
            .map(x -> GitSemverInstallTask.install(x))
            .map(x -> GitSemverCurrentVersionTask.command(x))
            .map(x -> Command.execute(x))
            .map(x -> {
                    if(x.exit == 0) {
                        return x
                    }
                    Loggy.error("Could not determine version: {}", x.serr)
                    return null
                })
            .map(x -> x.sout.trim())
            .orElseThrow(() -> new RuntimeException("Unable to run git-semver"))
    }

    static def command = Loggy.wrap({ x ->
        def mode = x.mode
        def commandParts = []
        commandParts.add(x.binary.getAbsolutePath())
        commandParts.add(mode)
        commandParts.add("-w ")
        commandParts.add(x.projectDir)
        x.command = commandParts.join(" ")
        return x
    })
        

}