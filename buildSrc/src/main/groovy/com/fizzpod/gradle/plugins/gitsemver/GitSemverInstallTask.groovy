/* (C) 2024 */
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
            .map(x -> GitSemverInstallTask.install(x))
            .orElseThrow(() -> new RuntimeException("Unable to install git-semver"))
    })

    static def install = Loggy.wrap({ x ->
        def repo = x.extension.repository
        def arch = x.extension.arch
        def os = x.extension.os
        def version = x.extension.version
        def location = x.location
        x.binary = GitSemverInstallation.install(repo, arch, os, version, location)
        return x.binary? x: null
    })

    static def location = Loggy.wrap({ x ->
        def projectDir = x.project.rootDir
        def semverDir = x.extension.location
        x.location = new File(projectDir, semverDir)
        return x.location? x: null
    })
}
