/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations

public abstract class GitSemverValueSource implements ValueSource<String, GitSemverValueSource.Params> {

    public static interface Params extends ValueSourceParameters {
        DirectoryProperty getProjectDir()
        Property<String> getSemverDir()
        Property<String> getRepository()
        Property<String> getToolVersion()
        Property<String> getArch()
        Property<String> getOs()
        Property<Long> getTtl()
        Property<Boolean> getStable()
        Property<String> getSnapshotSuffix()
    }

    @Inject
    protected abstract ExecOperations getExecOperations()

    @Override
    public String obtain() {
        def params = getParameters()
        def projectDirProv = params.getProjectDir()
        if (!projectDirProv.isPresent()) {
            return "0.0.0-NO-PROJECT-DIR"
        }
        def projectDir = projectDirProv.get().asFile
        def semverDirName = params.getSemverDir().getOrElse(".git-semver")
        def location = new File(projectDir, semverDirName)

        // Check if any binary exists within ttl
        def arch = OS.getArch(params.getArch().getOrNull())
        def os = OS.getOs(params.getOs().getOrNull())
        def ttl = params.getTtl().getOrElse(1000 * 60 * 60 * 24L)
        def binary = GitSemverInstallation.resolveTtl(location, arch, os, ttl)

        // Install if needed
        if (binary == null) {
            binary = install(location, params)
        }
        
        if (binary == null) {
            return "0.0.0-INSTALL-FAILED"
        }

        def context = [:]
        context.binary = binary
        context.projectDir = projectDir
        context.semverDir = semverDirName
        context.repository = params.getRepository().getOrNull()
        context.version = params.getToolVersion().getOrNull()
        context.os = params.getOs().getOrNull()
        context.arch = params.getArch().getOrNull()
        context.ttl = ttl
        context.stable = params.getStable().getOrElse(true)

        // Get Current Version
        def currentVersion = runGitSemver(getExecOperations(), context, "latest")
        if (currentVersion == null) {
             return "0.0.0-LATEST-FAILED"
        }

        // Get Next Version
        def nextVersion = runGitSemver(getExecOperations(), context, "next")
        if (nextVersion == null) {
             nextVersion = currentVersion // Fallback
        }

        // Check Git Status
        def hasChanges = checkGitStatus(projectDir)

        def snapshot = false
        if (hasChanges) {
            snapshot = true
        } else if (!nextVersion.equals(currentVersion)) {
            snapshot = true
        }

        def suffix = params.getSnapshotSuffix().getOrElse("-SNAPSHOT")
        return snapshot ? nextVersion + suffix : nextVersion
    }

    private File install(File location, Params params) {
         def repo = params.getRepository().getOrNull()
         def arch = params.getArch().getOrNull()
         def os = params.getOs().getOrNull()
         def version = params.getToolVersion().getOrNull()

         if (!repo || !version) {
             return null
         }

         try {
             return GitSemverInstallation.install(repo, arch, os, version, location)
         } catch (Exception e) {
             return null
         }
    }

    private String runGitSemver(ExecOperations execOperations, Map context, String mode) {
        context.mode = mode
        try {
            if (mode == "latest") {
                 return GitSemverCurrentVersionTask.run(execOperations, context)
            } else {
                 return GitSemverNextVersionTask.run(execOperations, context)
            }
        } catch (Exception e) {
            return null
        }
    }

    private boolean checkGitStatus(File projectDir) {
        def stdout = new ByteArrayOutputStream()
        try {
            def result = getExecOperations().exec {
                workingDir projectDir
                commandLine "git", "status", "--porcelain=v1"
                standardOutput = stdout
                ignoreExitValue = true
            }
            return result.exitValue == 0 && !stdout.toString().trim().isEmpty()
        } catch (Exception e) {
            return false
        }
    }
}
