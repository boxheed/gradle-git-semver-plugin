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

public class GitSemverStatusTask extends DefaultTask {

    public static final String NAME = "gitStatus"

    private Project project

    private String nextVersion

    @Inject
    public GitSemverStatusTask(Project project) {
        this.project = project
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        return taskContainer.create([name: NAME,
            type: GitSemverStatusTask,
            dependsOn: [],
            group: GitSemverPlugin.GROUP,
            description: 'Outputs the status of the current changes'])
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
        def status = runCommand(context)
        context.logger.lifecycle(status)
        return status
    }

    def createCommand(def context) {
        //git status --porcelain=v1 | grep -qE '^(.| )+ +\d+ +'
        def extension = context.extension
        def mode = context.mode
        def commandParts = []
        commandParts.add("git")
        commandParts.add("-C")
        commandParts.add(context.project.projectDir)
        commandParts.add("status")
        commandParts.add("--porcelain=v1")
        return commandParts.join(" ")
    }
        

}