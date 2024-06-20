package com.fizzpod.gradle.plugins.gitsemver

public class GitSemverPluginExtension {
	def version = "latest"
    def location = "git-semver"
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
    def snapshotSuffix = "-SNAPSHOT"
    def stable = true

    public class GitSemverVersionResolver {

        def nextVersion = null
        def currentVersion = null

        public String toString() {
            def snapshot = false
            installTask.runTask()
            if(this.currentVersion == null) {
                currentVersion =  semverTask.runTask()
            }
            if(this.nextVersion == null) {
                nextVersion =  nextSemverTask.runTask()
            }
            def status = statusTask.runTask()
            if(status != null && !"".equals(status.trim())) {
                snapshot = true
            } else if (!nextVersion.equals(currentVersion)){
                snapshot = true
            }

            return snapshot? nextVersion + snapshotSuffix: nextVersion

        }
    }
}
