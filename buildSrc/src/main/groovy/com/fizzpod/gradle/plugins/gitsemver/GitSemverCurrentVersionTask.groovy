package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import groovy.json.*
import javax.inject.Inject
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.io.FileUtils
import org.kohsuke.github.*

import static com.fizzpod.gradle.plugins.gitsemver.GitSemverInstallHelper.*
import static com.fizzpod.gradle.plugins.gitsemver.GitSemverRunnerTaskHelper.*

public class GitSemverCurrentVersionTask extends DefaultTask {

    public static final String NAME = "gitSemverCurrent"

    private Project project

    @Inject
    public GitSemverCurrentVersionTask(Project project) {
        this.project = project
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
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
        context.logger = project.getLogger()
        context.project = project
        context.extension = extension
        context.executable = getExecutable(context)
        context.mode = "latest"
        context.cmd = createCommand(context)
        def version = runCommand(context).trim()
        context.logger.lifecycle("Current version {}", version)
        return version
    }

    def createCommand(def context) {
        def extension = context.extension
        def mode = context.mode
        def commandParts = []
        commandParts.add(context.executable.getAbsolutePath())
        commandParts.add(mode)
        commandParts.add("-w ")
        commandParts.add(context.project.projectDir)
        return commandParts.join(" ")
    }
        

}