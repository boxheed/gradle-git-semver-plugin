/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import groovy.json.*
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

public class GitSemverNextVersionTask extends DefaultTask {

    public static final String NAME = "nextSemver"

    private Project project

    private String nextVersion

    @Inject
    public GitSemverNextVersionTask(Project project) {
        this.project = project
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        return taskContainer.create([name: NAME,
            type: GitSemverNextVersionTask,
            dependsOn: [],
            group: GitSemverPlugin.GROUP,
            description: 'Gets the next semantic version'])
    }

    @TaskAction
    def runTask() {
        def extension = project[GitSemverPlugin.NAME]
        def context = [:]
        context.project = project
        context.extension = extension
        def version = GitSemverNextVersionTask.run(context)
        project.logger.lifecycle(version)
    }

    static def run = { context ->
        context.mode = "next"
        def res = Optional.ofNullable(context)
            .map(x -> GitSemverInstallTask.location(x))
            .map(x -> GitSemverInstallTask.install(x))
            .map(x -> GitSemverNextVersionTask.command(x))
            .map(x -> Command.execute(x))
            .map(x -> {
                    if(x.exit == 0) {
                        return x
                    }
                    Loggy.error("Could not determine next version: {}", x.serr)
                    return null
                })
            .map(x -> x.sout.trim())
            .orElseThrow(() -> new RuntimeException("Unable to run git-semver"))
    }

    static def command = { x ->
        def extension = x.extension
        def mode = x.mode
        def commandParts = []
        commandParts.add(x.binary.getAbsolutePath())
        commandParts.add(mode)
        commandParts.add("--stable=" + extension.stable)
        commandParts.add("-w ")
        commandParts.add(x.project.projectDir)
        x.command = commandParts.join(" ")
        return x
    }

}
