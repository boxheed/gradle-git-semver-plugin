/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction


public class GitSemverInstallAllTask extends DefaultTask {

    public static final String NAME = "installAllSemvers"

    private def osArches = [
        [OS.Family.LINUX.id, OS.Arch.AMD64.id],
        [OS.Family.LINUX.id, OS.Arch.ARM64.id],
        [OS.Family.MAC.id, OS.Arch.AMD64.id],
        [OS.Family.MAC.id, OS.Arch.ARM64.id],
        [OS.Family.WINDOWS.id, OS.Arch.AMD64.id]
    ]

    @Inject
    public GitSemverInstallAllTask() {
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
        def extension = project.extensions.getByName(GitSemverPlugin.NAME)
        
        for(def osArch: osArches) {
            def osVal = osArch[0]
            def archVal = osArch[1]
            Loggy.lifecycle("Installing {} : {}", osVal, archVal)
            def context = [:]
            context.projectDir = project.rootDir
            context.semverDir = extension.location.get()
            context.repository = extension.repository.get()
            context.version = extension.version.get()
            context.os = osVal
            context.arch = archVal
            context.ttl = extension.ttl.get()
            
            GitSemverInstallTask.run(context)
        }
    }

}