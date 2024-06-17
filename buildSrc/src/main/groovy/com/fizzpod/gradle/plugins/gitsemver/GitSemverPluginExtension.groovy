package com.fizzpod.gradle.plugins.osvscanner

public class GitSemverPluginExtension {
	def version = "latest"
    def location = "git-semver"
    def repository = "PSanetra/git-semver"
    def os = null
    def arch = null
    def flags = ""
    def binary = ""
}
