package com.fizzpod.gradle.plugins.gitsemver

public class GitSemverPluginExtension {
	def version = "latest"
    def location = ".git-semver"
    def repository = "PSanetra/git-semver"
    def os = null
    def arch = null
    def flags = ""
    def binary = ""
    def project = null
    def resolve = new GitSemverVersionResolver()
    def installTask = null
    def semverTask = null
    def nextSemverTask = null
    def statusTask = null
    def tagTask = null
    def snapshotSuffix = "-SNAPSHOT"
    def stable = true

    public class GitSemverVersionResolver {

        def nextVersion = null
        def currentVersion = null

        public String toString() {
            def extension = project[GitSemverPlugin.NAME]
            def context = [:]
            context.project = project
            context.extension = extension
            def snapshot = false
            installTask.runTask()
            if(this.currentVersion == null) {
                currentVersion =  GitSemverCurrentVersionTask.run(context)
            }
            if(this.nextVersion == null) {
                nextVersion =  GitSemverNextVersionTask.run(context)
            }
            def status = statusTask.run(context)
            if(status != null && !"".equals(status.trim())) {
                snapshot = true
            } else if (!nextVersion.equals(currentVersion)){
                snapshot = true
            }

            return snapshot? nextVersion + snapshotSuffix: nextVersion

        }
    }
}
