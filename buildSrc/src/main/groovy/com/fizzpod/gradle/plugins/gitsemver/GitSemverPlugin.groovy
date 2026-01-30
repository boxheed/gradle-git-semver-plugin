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
            version.set(extension.version)
            location.set(extension.location)
            repository.set(extension.repository)
            os.set(extension.os)
            arch.set(extension.arch)
            ttl.set(extension.ttl)
        }
		def installAllTask = GitSemverInstallAllTask.register(project)
		def currentVersionTask = GitSemverCurrentVersionTask.register(project)
		def nextVersionTask = GitSemverNextVersionTask.register(project)
		def statusTask = GitSemverStatusTask.register(project)
		def tagTask = GitSemverTagTask.register(project)
	}
}
