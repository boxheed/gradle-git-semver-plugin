/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "This task requires git-semver execution output")
public abstract class GitSemverInstallAllTask extends DefaultTask {

    public static final String NAME = "installAllSemvers"

    @PathSensitive(PathSensitivity.RELATIVE)
    @InputDirectory
    abstract DirectoryProperty getProjectDir()

    @Input
    abstract Property<String> getSemverDir()

    @Input
    abstract Property<String> getRepository()

    @Input
    abstract Property<String> getToolVersion()

    @Input
    abstract Property<Long> getTtl()

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

        return taskContainer.register(NAME, GitSemverInstallAllTask) {
            it.group = GitSemverPlugin.GROUP
            it.description = 'Download and install all git-semver binaries'
        }
    }

    @TaskAction
    def runTask() {
        def projectDir = getProjectDir().get().asFile
        def semverDir = getSemverDir().get()
        def repository = getRepository().get()
        def version = getToolVersion().get()
        def ttl = getTtl().get()
        
        for(def osArch: osArches) {
            def osVal = osArch[0]
            def archVal = osArch[1]
            Loggy.lifecycle("Installing {} : {}", osVal, archVal)
            def context = [:]
            context.projectDir = projectDir
            context.semverDir = semverDir
            context.repository = repository
            context.version = version
            context.os = osVal
            context.arch = archVal
            context.ttl = ttl
            
            GitSemverInstallTask.run(context)
        }
    }

}
