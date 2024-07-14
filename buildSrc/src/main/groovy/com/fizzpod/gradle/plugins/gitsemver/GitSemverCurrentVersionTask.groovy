package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import groovy.json.*
import javax.inject.Inject
import org.kohsuke.github.*

import static com.fizzpod.gradle.plugins.gitsemver.GitSemverRunnerTaskHelper.*

public class GitSemverCurrentVersionTask extends DefaultTask {

    public static final String NAME = "semver"

    private Project project

    @Inject
    public GitSemverCurrentVersionTask(Project project) {
        this.project = project
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
        def extension = project[GitSemverPlugin.NAME]
        def context = [:]
        context.project = project
        context.extension = extension
        def version = GitSemverCurrentVersionTask.run(context)
        project.logger.lifecycle(version)
    }

    static def run = { context ->
        context.mode = "latest"
        def res = Optional.ofNullable(context)
            .map(x -> GitSemverInstallTask.location(x))
            .map(x -> GitSemverInstallTask.install(x))
            .map(x -> GitSemverCurrentVersionTask.command(x))
            .map(x -> GitSemverCurrentVersionTask.execute(x))
            .map(x -> x.sout.trim())
            .orElseThrow(() -> new RuntimeException("Unable to run git-semver"))
    }

    static def execute = Loggy.wrap({ x ->
        x = x + GitSemverRunnerTaskHelper.run(x.command)
        return x
    })
    
    static def command = Loggy.wrap({ x ->
        def extension = x.extension
        def mode = x.mode
        def commandParts = []
        commandParts.add(x.binary.getAbsolutePath())
        commandParts.add(mode)
        commandParts.add("-w ")
        commandParts.add(x.project.projectDir)
        x.command = commandParts.join(" ")
        return x
    })
        

}