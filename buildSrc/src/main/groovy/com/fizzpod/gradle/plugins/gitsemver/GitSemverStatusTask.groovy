package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
//import groovy.json.*
import javax.inject.Inject
import org.kohsuke.github.*

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
        context.project = project
        context.extension = extension
        def changes = GitSemverStatusTask.run(context)
        if(changes.exit == 0) {
            Loggy.lifecycle("Git status: \n{}", changes.sout? changes.sout: "No Changes")
        } else {
            Loggy.lifecycle("Git status error: \n{}\n{}", changes.serr, changes.serr)

        }
    }

    static def run = { context ->
        def status = Optional.ofNullable(context)
            .map(x -> GitSemverStatusTask.command(x))
            .map(x -> GitSemverCurrentVersionTask.execute(x))
            .orElseThrow(() -> new RuntimeException("Unable to run git-semver"))
        return status
    }

    static def getOut = Loggy.wrap( { x -> 
            def out = x.sout? x.sout.trim(): ""
            return out
        })

    static def command = Loggy.wrap( { x ->
        //git status --porcelain=v1 | grep -qE '^(.| )+ +\d+ +'
        def commandParts = []
        commandParts.add("git")
        commandParts.add("-C")
        commandParts.add(x.project.projectDir)
        commandParts.add("status")
        commandParts.add("--porcelain=v1")
        x.command = commandParts.join(" ")
        return x
    } )
        

}