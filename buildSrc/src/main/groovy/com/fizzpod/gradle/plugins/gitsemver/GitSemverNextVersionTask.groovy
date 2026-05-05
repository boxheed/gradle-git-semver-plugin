/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import groovy.json.*
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional as OptionalTask
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask
import org.gradle.process.ExecOperations

@UntrackedTask(because = "This task requires git-semver execution output")
public abstract class GitSemverNextVersionTask extends DefaultTask {

    public static final String NAME = "nextSemver"

    @PathSensitive
    @InputDirectory
    abstract DirectoryProperty getProjectDir()

    @Input
    abstract Property<String> getSemverDir()

    @Input
    abstract Property<String> getRepository()

    @Input
    abstract Property<String> getToolVersion()

    @Input
    @OptionalTask
    abstract Property<String> getOs()

    @Input
    @OptionalTask
    abstract Property<String> getArch()

    @Input
    abstract Property<Long> getTtl()

    @Input
    abstract Property<Boolean> getStable()

    @Inject
    protected abstract ExecOperations getExecOperations()

    @Inject
    public GitSemverNextVersionTask() {
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        return taskContainer.register(NAME, GitSemverNextVersionTask) {
            it.group = GitSemverPlugin.GROUP
            it.description = 'Gets the next semantic version'
        }
    }

    @TaskAction
    public void runTask() {
        def context = [:]
        
        context.projectDir = getProjectDir().get().asFile
        context.semverDir = getSemverDir().get()
        context.repository = getRepository().get()
        context.version = getToolVersion().get()
        context.os = getOs().getOrNull()
        context.arch = getArch().getOrNull()
        context.ttl = getTtl().get()
        context.stable = getStable().get()
        
        def version = run(getExecOperations(), context)
        project.logger.lifecycle(version)
    }

    public static String run(ExecOperations execOperations, Map context) {
        context.mode = "next"
        def res = Optional.ofNullable(context)
            .map(x -> GitSemverInstallTask.location(x))
            .map(x -> GitSemverInstallTask.ttl(x))
            .map(x -> GitSemverInstallTask.install(x))
            .map(x -> GitSemverNextVersionTask.command(x))
            .map(x -> Command.execute(execOperations, x))
            .map(x -> {
                    if(x.exit == 0) {
                        return x
                    }
                    Loggy.error("Could not determine next version: {}", x.serr)
                    return null
                })
            .map(x -> x.sout.trim())
            .orElseThrow(() -> new RuntimeException("Unable to run git-semver"))
        return res
    }

    static def command = { x ->
        def mode = x.mode
        def commandParts = []
        commandParts.add(x.binary.getAbsolutePath())
        commandParts.add(mode)
        commandParts.add("--stable=" + x.stable)
        commandParts.add("-w")
        commandParts.add(x.projectDir.getAbsolutePath())
        x.commandParts = commandParts
        return x
    }

}
