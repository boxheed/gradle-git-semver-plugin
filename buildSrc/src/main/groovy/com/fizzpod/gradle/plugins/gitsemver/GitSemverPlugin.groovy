package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Plugin
import org.gradle.api.Project

public class GitSemverPlugin implements Plugin<Project> {

	public static final String NAME = "getSemver"
	public static final String GROUP = "Git Semver"
	public static final String EXE_NAME = "git-semver"

	void apply(Project project) {
		project.extensions.create(NAME, GitSemverPluginExtension)
		GitSemverInstallTask.register(project)
		GitSemverInstallAllTask.register(project)
		GitSemverCurrentVersionTask.register(project)
		GitSemverNextVersionTask.register(project)
	}
}