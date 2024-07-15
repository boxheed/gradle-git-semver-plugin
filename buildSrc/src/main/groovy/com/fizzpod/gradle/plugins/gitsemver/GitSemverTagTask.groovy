package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

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
            Loggy.lifecycle("Tagged repository withs: \n{}", tag.version)
        } else {
            Loggy.lifecycle("Failed to tag repository: \n{}\n{}", tag.serr, tag.serr)
        }
    }

    static def run = { context ->
        def status = Optional.ofNullable(context)
            .map(x -> GitSemverTagTask.isClean(x))
            .map(x -> GitSemverTagTask.version(x))
            .map(x -> GitSemverTagTask.command(x))
            .map(x -> GitSemverCurrentVersionTask.execute(x))
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
            x.clean = true
        if(result.exit == 0 && result.sout.trim() == "") {
        } else {
            Loggy.error("Repository isn't clean: \n{}\n{}", x.status.sout, x.status.serr)
        }
        x.clean? x: null
    })

/*
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
*/
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