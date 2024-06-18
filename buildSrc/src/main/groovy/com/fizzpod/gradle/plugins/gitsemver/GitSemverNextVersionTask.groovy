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

public class GitSemverNextVersionTask extends DefaultTask {

    public static final String NAME = "gitSemverNext"

    private Project project

    @Inject
    public GitSemverNextVersionTask(Project project) {
        this.project = project
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        taskContainer.create([name: NAME,
            type: GitSemverNextVersionTask,
            dependsOn: [],
            group: GitSemverPlugin.GROUP,
            description: 'Gets the next semantic version'])
    }

    @TaskAction
    def runTask() {
        def extension = project[GitSemverPlugin.NAME]
        def context = [:]
        context.logger = project.getLogger()
        context.project = project
        context.extension = extension
        context.executable = getExecutable(context)
        context.mode = "next"
        context.cmd = createCommand(context)
        def version = runCommand(context)
        context.logger.lifecycle("Next version {}", version)
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