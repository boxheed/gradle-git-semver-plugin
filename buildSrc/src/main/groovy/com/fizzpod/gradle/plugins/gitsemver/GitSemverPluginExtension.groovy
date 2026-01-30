/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import javax.inject.Inject
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

public abstract class GitSemverPluginExtension {

    abstract Property<String> getVersion()
    abstract Property<String> getLocation()
    abstract Property<String> getRepository()
    abstract Property<String> getPrefix()
    abstract Property<String> getOs()
    abstract Property<String> getArch()
    abstract Property<String> getFlags()
    abstract Property<File> getBinary()

    abstract Property<String> getSnapshotSuffix()
    abstract Property<Long> getTtl()
    abstract Property<Boolean> getStable()

    // New provider
    abstract Property<String> getComputedVersion()

    def project = null
    def resolve = new GitSemverVersionResolver()

    // Tasks
    def installTask = null
    def semverTask = null
    def nextSemverTask = null
    def statusTask = null
    def tagTask = null

    @Inject
    public GitSemverPluginExtension(ObjectFactory objects, ProviderFactory providers, ProjectLayout layout) {
        getVersion().convention("latest")
        getLocation().convention(".git-semver")
        getRepository().convention("PSanetra/git-semver")
        getPrefix().convention("v")
        getFlags().convention("")
        getSnapshotSuffix().convention("-SNAPSHOT")
        getTtl().convention(1000 * 60 * 60 * 24L)
        getStable().convention(true)

        // Configure computedVersion
        getComputedVersion().set(providers.of(GitSemverValueSource) { spec ->
            spec.parameters.projectDir.set(layout.getProjectDirectory())
            spec.parameters.semverDir.set(getLocation())
            spec.parameters.repository.set(getRepository())
            spec.parameters.toolVersion.set(getVersion())
            spec.parameters.arch.set(getArch())
            spec.parameters.os.set(getOs())
            spec.parameters.ttl.set(getTtl())
            spec.parameters.stable.set(getStable())
            spec.parameters.snapshotSuffix.set(getSnapshotSuffix())
        })
    }

    @Deprecated
    public class GitSemverVersionResolver {

        def nextVersion = null
        def currentVersion = null

        public String toString() {
            return GitSemverPluginExtension.this.getComputedVersion().get()
        }
    }
}
