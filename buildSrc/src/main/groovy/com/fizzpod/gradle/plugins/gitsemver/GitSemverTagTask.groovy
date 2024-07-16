package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

public class GitSemverTagTask extends DefaultTask {

    public static final String NAME = "tagSemver"

    private Project project


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
            description: 'Tags the repository with the next semantic version'])
    }

    @TaskAction
    def runTask() {

        def extension = project[GitSemverPlugin.NAME]
        def context = [:]
        context.project = project
        context.extension = extension
        def tag = GitSemverTagTask.run(context)
        
        if(tag.exit == 0) {
            Loggy.lifecycle("Tagged repository with: \n{}", tag.version)
        } else {
            Loggy.lifecycle("Failed to tag repository: \n{}\n{}", tag.serr, tag.serr)
            throw new RuntimeException("Unable to tag repository")
        }
    }

    static def run = { context ->
        def status = Optional.ofNullable(context)
            .map(x -> GitSemverTagTask.isClean(x))
            .map(x -> GitSemverTagTask.version(x))
            .map(x -> GitSemverTagTask.command(x))
            .map(x -> Command.execute(x))
            .orElseThrow(() -> new RuntimeException("Unable to tag repository"))
        return status
    }

    static def version = Loggy.wrap({ x -> 
        def context = [:]
        context = context + x
        x.version = x.extension.prefix + GitSemverNextVersionTask.run(context)
        x.version? x: null
    })

    static def isClean = Loggy.wrap({ x -> 
        def context = [:]
        context = context + x
        x.clean = false
        def result = GitSemverStatusTask.run(context)
        x.status = [exit: result.exit, sout: result.sout, serr: result.serr]
        if(result.exit == 0 && result.sout.trim() == "") {
            Loggy.debug("Local repository is clean; nothing to commit")
            x.clean = true
        } else {
            Loggy.error("Repository is not clean: \n{}\n{}", x.status.sout, x.status.serr)
        }
        x.clean? x: null
    })

    static def command = Loggy.wrap( { x ->
        def extension = x.extension
        def commandParts = []
        commandParts.add("git")
        commandParts.add("-C")
        commandParts.add(x.project.projectDir)
        commandParts.add("tag")
        commandParts.add(x.version)
        x.command = commandParts.join(" ")
        return x
    })
        

}