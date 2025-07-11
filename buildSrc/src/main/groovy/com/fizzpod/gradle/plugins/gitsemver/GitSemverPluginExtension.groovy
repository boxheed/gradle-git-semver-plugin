/* (C) 2024-2025 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

public class GitSemverPluginExtension {
	def version = "latest"
    def location = ".git-semver"
    def repository = "PSanetra/git-semver"
    def prefix = "v"
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
    def ttl = 1000 * 60 * 60 * 24
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
            currentVersion = GitSemverCurrentVersionTask.run(context)
            nextVersion = GitSemverNextVersionTask.run(context)
            def status = statusTask.run(context)
            if(status.sout != "") {
                snapshot = true
            } else if (!nextVersion.equals(currentVersion)){
                snapshot = true
            }

            return snapshot? nextVersion + snapshotSuffix: nextVersion

        }
    }
}
