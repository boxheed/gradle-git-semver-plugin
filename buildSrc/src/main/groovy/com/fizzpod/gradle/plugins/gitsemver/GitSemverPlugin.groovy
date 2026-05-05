/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Plugin
import org.gradle.api.Project

public class GitSemverPlugin implements Plugin<Project> {

	public static final String NAME = "gitSemver"
	public static final String GROUP = "Git Semver"
	public static final String EXE_NAME = "git-semver"

	void apply(Project project) {
		GitSemverPluginExtension extension = project.extensions.create(NAME, GitSemverPluginExtension)
		
		def installTask = GitSemverInstallTask.register(project)
        installTask.configure {
            it.projectDir.set(project.getLayout().getProjectDirectory())
            it.version.set(extension.version)
            it.location.set(extension.location)
            it.repository.set(extension.repository)
            it.os.set(extension.os)
            it.arch.set(extension.arch)
            it.ttl.set(extension.ttl)
        }
		
		def installAllTask = GitSemverInstallAllTask.register(project)
        installAllTask.configure {
            it.projectDir.set(project.getLayout().getProjectDirectory())
            it.semverDir.set(extension.location)
            it.repository.set(extension.repository)
            it.toolVersion.set(extension.version)
            it.ttl.set(extension.ttl)
        }

		def currentVersionTask = GitSemverCurrentVersionTask.register(project)
        currentVersionTask.configure {
            it.projectDir.set(project.getLayout().getProjectDirectory())
            it.semverDir.set(extension.location)
            it.repository.set(extension.repository)
            it.toolVersion.set(extension.version)
            it.os.set(extension.os)
            it.arch.set(extension.arch)
            it.ttl.set(extension.ttl)
            it.stable.set(extension.stable)
        }

		def nextVersionTask = GitSemverNextVersionTask.register(project)
        nextVersionTask.configure {
            it.projectDir.set(project.getLayout().getProjectDirectory())
            it.semverDir.set(extension.location)
            it.repository.set(extension.repository)
            it.toolVersion.set(extension.version)
            it.os.set(extension.os)
            it.arch.set(extension.arch)
            it.ttl.set(extension.ttl)
            it.stable.set(extension.stable)
        }

		def statusTask = GitSemverStatusTask.register(project)
        statusTask.configure {
            it.projectDir.set(project.getLayout().getProjectDirectory())
        }

		def tagTask = GitSemverTagTask.register(project)
        tagTask.configure {
            it.projectDir.set(project.getLayout().getProjectDirectory())
            it.semverDir.set(extension.location)
            it.repository.set(extension.repository)
            it.toolVersion.set(extension.version)
            it.os.set(extension.os)
            it.arch.set(extension.arch)
            it.ttl.set(extension.ttl)
            it.stable.set(extension.stable)
            it.prefix.set(extension.prefix)
        }
	}
}
