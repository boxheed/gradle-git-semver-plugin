/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional as OptionalTask
import org.gradle.api.tasks.TaskAction

public abstract class GitSemverInstallTask extends DefaultTask {

    public static final String NAME = "installSemver"

    public static final String GITSEMVER_INSTALL_DIR = ".git-semver"

    @Input
    abstract Property<String> getVersion()
    @Input
    abstract Property<String> getLocation()
    @Input
    abstract Property<String> getRepository()
    @Input
    @OptionalTask
    abstract Property<String> getOs()
    @Input
    @OptionalTask
    abstract Property<String> getArch()
    @Input
    abstract Property<Long> getTtl()

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        return taskContainer.create(NAME, GitSemverInstallTask) {
            group = GitSemverPlugin.GROUP
            description = 'Downloads and installs git-semver'
        }
    }

    @TaskAction
    def runTask() {
        def context = [:]
        context.projectDir = project.rootDir
        context.semverDir = getLocation().get()
        context.repository = getRepository().get()
        context.version = getVersion().get()
        context.os = getOs().getOrNull()
        context.arch = getArch().getOrNull()
        context.ttl = getTtl().get()
        
        GitSemverInstallTask.run(context)
    }

    static def run = Loggy.wrap({ context ->
        return java.util.Optional.ofNullable(context)
            .map(x -> GitSemverInstallTask.location(x))
            .map(x -> GitSemverInstallTask.ttl(x))
            .map(x -> GitSemverInstallTask.install(x))
            .orElseThrow(() -> new RuntimeException("Unable to install git-semver"))
    })

    /**
    * Find the most recent binary and see if it is within ttl
    */
    static def ttl = { x ->
        // Previously read from extension.binary. Now we assume passed in context or resolved.
        def binary = x.binary
        if(binary == null || !binary.exists()) {
            def location = x.location
            def arch = OS.getArch(x.arch)
            def os = OS.getOs(x.os)
            def ttl = x.ttl
            binary = GitSemverInstallation.resolveTtl(location, arch, os, ttl)
        }
        if(binary != null && binary.exists()) {
            x.binary = binary
        }
        return x
    }

    static def install = Loggy.wrap({ x ->
        def repo = x.repository
        def arch = x.arch
        def os = x.os
        def version = x.version
        def location = x.location
        if(!x.binary || !x.binary.exists()) {
            x.binary = GitSemverInstallation.install(repo, arch, os, version, location)
        }
        return x.binary? x: null
    })

    static def location = Loggy.wrap({ x ->
        def projectDir = x.projectDir
        def semverDir = x.semverDir
        x.location = new File(projectDir, semverDir)
        return x.location? x: null
    })
}