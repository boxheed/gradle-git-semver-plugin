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

public class GitSemverInstallAllTask extends GitSemverInstallTask {

    public static final String NAME = "installAllGitSemvers"

    private Project project
    private def osArches = [
        [LINUX, AMD64],
        [LINUX, ARM64],
        [MAC, AMD64],
        [MAC, ARM64],
        [WINDOWS, AMD64]
    ]

    private def currentOs;
    private def currentArch;

    @Inject
    public GitSemverInstallAllTask(Project project) {
        super(project)
        this.project = project
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        taskContainer.create([name: NAME,
            type: GitSemverInstallAllTask,
            dependsOn: [],
            group: GitSemverPlugin.GROUP,
            description: 'Downloads and installs all osv-scanner binaries'])
    }

    @TaskAction
    def runTask() {
        for(def osArch: osArches) {
            currentOs = osArch[0]
            currentArch = osArch[1]
            project.getLogger().lifecycle("Installing {} : {}", currentOs, currentArch)
            super.runTask()
        }
    }

    def getAsset(def context) {
        context.os = currentOs
        context.arch = currentArch
        return super.getAsset(context)
    }
    
    def install(def context) {
        context.os = currentOs
        context.arch = currentArch
        super.install(context)
    }
    
    def download(def context) {
        context.os = currentOs
        context.arch = currentArch
        super.download(context)
    }
    

}