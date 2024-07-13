package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
//import groovy.json.*
import javax.inject.Inject

import static com.fizzpod.gradle.plugins.gitsemver.GitSemverRunnerTaskHelper.*

public class GitSemverTagTask extends DefaultTask {

    public static final String NAME = "tagSemver"

    private Project project

    private String nextVersion

    @Inject
    public GitSemverTagTask(Project project) {
        this.project = project
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        return taskContainer.create([name: NAME,
            type: GitSemverTagTask,
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
        def status = extension.statusTask.runTask()
        if(status != null && !"".equals(status.trim())) {
            context.logger.error("Unable to tag repository as local repo is dirty")
            //throw new RuntimeException("Unable to tag repository as local repo is dirty")
        }
        context.version = "v" + extension.nextSemverTask.runTask()
        context.cmd = createCommand(context)
        runCommand(context)
        context.logger.lifecycle("Tagged repository with {}", context.version)
        
    }

    def createCommand(def context) {
        //git status --porcelain=v1 | grep -qE '^(.| )+ +\d+ +'
        def extension = context.extension
        def mode = context.mode
        def commandParts = []
        commandParts.add("git")
        commandParts.add("-C")
        commandParts.add(context.project.projectDir)
        commandParts.add("tag")
        commandParts.add(context.version)
        return commandParts.join(" ")
    }
        

}