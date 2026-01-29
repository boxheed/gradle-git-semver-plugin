/* (C) 2024-2025 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import javax.inject.Inject
import org.gradle.api.provider.Property
import org.gradle.api.model.ObjectFactory

public abstract class GitSemverPluginExtension {

    abstract Property<String> getVersion()
    abstract Property<String> getLocation()
    abstract Property<String> getRepository()
    abstract Property<String> getPrefix()
    abstract Property<String> getOs()
    abstract Property<String> getArch()
    abstract Property<String> getFlags()
    abstract Property<File> getBinary()

    def project = null
    def resolve = new GitSemverVersionResolver()
    def installTask = null
    def semverTask = null
    def nextSemverTask = null
    def statusTask = null
    def tagTask = null

    abstract Property<String> getSnapshotSuffix()
    abstract Property<Long> getTtl()
    abstract Property<Boolean> getStable()

    @Inject
    public GitSemverPluginExtension(ObjectFactory objects) {
        getVersion().convention("latest")
        getLocation().convention(".git-semver")
        getRepository().convention("PSanetra/git-semver")
        getPrefix().convention("v")
        getFlags().convention("")
        getSnapshotSuffix().convention("-SNAPSHOT")
        getTtl().convention(1000 * 60 * 60 * 24L)
        getStable().convention(true)
    }

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

            return snapshot? nextVersion + extension.snapshotSuffix.get(): nextVersion

        }
    }
}
