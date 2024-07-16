package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Plugin
import org.gradle.api.Project

public class GitSemverPlugin implements Plugin<Project> {

	public static final String NAME = "gitSemver"
	public static final String GROUP = "Git Semver"
	public static final String EXE_NAME = "git-semver"

	void apply(Project project) {
		GitSemverPluginExtension extension = project.extensions.create(NAME, GitSemverPluginExtension)
		extension.project = project
		def installTask = GitSemverInstallTask.register(project)
		def installAllTask = GitSemverInstallAllTask.register(project)
		def currentVersionTask = GitSemverCurrentVersionTask.register(project)
		def nextVersionTask = GitSemverNextVersionTask.register(project)
		def statusTask = GitSemverStatusTask.register(project)
		def tagTask = GitSemverTagTask.register(project)
		extension.installTask = installTask
		extension.semverTask = currentVersionTask
		extension.nextSemverTask = nextVersionTask
		extension.statusTask = statusTask
		extension.tagTask = tagTask
	}
}