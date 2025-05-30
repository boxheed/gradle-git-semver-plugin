/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction


public class GitSemverInstallAllTask extends DefaultTask {

    public static final String NAME = "installAllSemvers"

    private Project project
    private def osArches = [
        [OS.Family.LINUX.id, OS.Arch.AMD64.id],
        [OS.Family.LINUX.id, OS.Arch.ARM64.id],
        [OS.Family.MAC.id, OS.Arch.AMD64.id],
        [OS.Family.MAC.id, OS.Arch.ARM64.id],
        [OS.Family.WINDOWS.id, OS.Arch.AMD64.id]
    ]

    @Inject
    public GitSemverInstallAllTask(Project project) {
        this.project = project
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        taskContainer.create([name: NAME,
            type: GitSemverInstallAllTask,
            dependsOn: [],
            group: GitSemverPlugin.GROUP,
            description: 'Download and install all git-semver binaries'])
    }

    @TaskAction
    def runTask() {
        def extension = project[GitSemverPlugin.NAME]
        def originalOs = extension.os
        def originalArch = extension.arch
        for(def osArch: osArches) {
            extension.os = osArch[0]
            extension.arch = osArch[1]
            Loggy.lifecycle("Installing {} : {}", extension.os, extension.arch)
            def context = [:]
            context.project = project
            context.extension = extension
            GitSemverInstallTask.run(context)
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
