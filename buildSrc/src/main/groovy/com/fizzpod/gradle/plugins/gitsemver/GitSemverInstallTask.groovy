/* (C) 2024-2025 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

public class GitSemverInstallTask extends DefaultTask {

    public static final String NAME = "installSemver"

    public static final String GITSEMVER_INSTALL_DIR = ".git-semver"

    private Project project

    @Inject
    public GitSemverInstallTask(Project project) {
        this.project = project
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        return taskContainer.create([name: NAME,
            type: GitSemverInstallTask,
            dependsOn: [],
            group: GitSemverPlugin.GROUP,
            description: 'Downloads and installs git-semver'])
    }

    @TaskAction
    def runTask() {
        def extension = project[GitSemverPlugin.NAME]
        def context = [:]
        context.project = project
        context.extension = extension
        GitSemverInstallTask.run(context)
    }

    static def run = Loggy.wrap({ context ->
        return Optional.ofNullable(context)
            .map(x -> GitSemverInstallTask.location(x))
            .map(x -> GitSemverInstallTask.ttl(x))
            .map(x -> GitSemverInstallTask.install(x))
            .orElseThrow(() -> new RuntimeException("Unable to install git-semver"))
    })

        /**
    * Find the most recent binary and see if it is within ttl
    */
    static def ttl = { x ->
        def binary = x.extension.binary.getOrNull()
        if(binary == null || !binary.exists()) {
            def location = x.location
            def arch = OS.getArch(x.extension.arch.getOrNull())
            def os = OS.getOs(x.extension.os.getOrNull())
            def ttl = x.extension.ttl.get()
            binary = GitSemverInstallTask.resolveTtl(location, arch, os, ttl)
        }
        if(binary != null && binary.exists()) {
            x.extension.binary.set(binary)
            x.binary = binary
        }
        return x
    }

    static def resolveTtl = {  File location, OS.Arch arch, OS.Family os, long ttl ->
        def latestBinary = null
        def currentTime = System.currentTimeMillis()
        def binaryPattern = GitSemverInstallation.getBinaryName("v?(\\d+\\.\\d+\\.\\d+)", os, arch) + ".*"
        location.listFiles().each { File file ->
            if (file.name =~ binaryPattern) {
                Loggy.info("Checking ${file.name}")
                def lastModified = file.lastModified()
                def timeDiff = currentTime - lastModified
                if (timeDiff < ttl) { 
                    Loggy.info("${file.name} within ttl of ${ttl}")
                    if(latestBinary != null && latestBinary.lastModified() < file.lastModified()) {
                        latestBinary = file
                    } else if (latestBinary == null){
                        latestBinary = file
                    }
                } else {
                    Loggy.info("${file.name} outide ttl of ${ttl}")
                }
            }
        }
        if(latestBinary == null) {
            Loggy.info("gitsemver not found")
        } else {
            Loggy.info("Using gitsemver ${latestBinary}")
        }
        return latestBinary
    }

    static def install = Loggy.wrap({ x ->
        def repo = x.extension.repository.get()
        def arch = x.extension.arch.getOrNull()
        def os = x.extension.os.getOrNull()
        def version = x.extension.version.get()
        def location = x.location
        if(!x.binary || !x.binary.exists()) {
            x.binary = GitSemverInstallation.install(repo, arch, os, version, location)
        }
        return x.binary? x: null
    })

    static def location = Loggy.wrap({ x ->
        def projectDir = x.project.rootDir
        def semverDir = x.extension.location.get()
        x.location = new File(projectDir, semverDir)
        return x.location? x: null
    })
}
