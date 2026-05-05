/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

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
public abstract class GitSemverTagTask extends DefaultTask {

    public static final String NAME = "tagSemver"

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

    @Input
    abstract Property<String> getPrefix()

    @Inject
    protected abstract ExecOperations getExecOperations()

    @Inject
    public GitSemverTagTask() {
    }


    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        return taskContainer.register(NAME, GitSemverTagTask) {
            it.group = GitSemverPlugin.GROUP
            it.description = 'Tags the repository with the next semantic version'
        }
    }

    @TaskAction
    public void runTask() {
        def context = [: ]
        
        context.projectDir = getProjectDir().get().asFile
        context.semverDir = getSemverDir().get()
        context.repository = getRepository().get()
        context.version = getToolVersion().get()
        context.os = getOs().getOrNull()
        context.arch = getArch().getOrNull()
        context.ttl = getTtl().get()
        context.stable = getStable().get()
        context.prefix = getPrefix().get()

        def tag = run(getExecOperations(), context)
        
        if(tag.exit == 0) {
            Loggy.lifecycle("Tagged repository with: \n{}", tag.version)
        } else {
            Loggy.lifecycle("Failed to tag repository: \n{}\n{}", tag.serr, tag.serr)
            throw new RuntimeException("Unable to tag repository")
        }
    }

    public static Map run(ExecOperations execOperations, Map context) {
        def status = Optional.ofNullable(context)
            .map(x -> isClean(execOperations, x))
            .map(x -> version(execOperations, x))
            .map(x -> command(x))
            .map(x -> Command.execute(execOperations, x))
            .orElseThrow(() -> new RuntimeException("Unable to tag repository"))
        return status
    }

    private static Map version(ExecOperations execOperations, Map x) { 
        def context = [: ]
        context = context + x
        x.version = x.prefix + GitSemverNextVersionTask.run(execOperations, context)
        return x.version? x: null
    }

    private static Map isClean(ExecOperations execOperations, Map x) { 
        def context = [: ]
        context = context + x
        x.clean = false
        def result = Command.runInDir(execOperations, ["git", "status", "--porcelain=v1"], x.projectDir)
        x.status = [exit: result.exit, sout: result.sout, serr: result.serr]
        if(result.exit == 0 && result.sout.trim() == "") {
            Loggy.debug("Local repository is clean; nothing to commit")
            x.clean = true
        } else {
            Loggy.error("Repository is not clean: \n{}\n{}", x.status.sout, x.status.serr)
        }
        return x.clean? x: null
    }

    static def command = Loggy.wrap({ x ->
        def commandParts = []
        commandParts.add("git")
        commandParts.add("-C")
        commandParts.add(x.projectDir.getAbsolutePath())
        commandParts.add("tag")
        commandParts.add(x.version)
        x.commandParts = commandParts
        return x
    })
        

}
