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
        def projectDir = params.getProjectDir().get().asFile
        def semverDirName = params.getSemverDir().get()
        def location = new File(projectDir, semverDirName)

        // Install if needed
        def binary = install(location, params)
        if (binary == null) {
            // Fallback or error?
            // If we can't install, we can't determine version.
            return "0.0.0-FAILED"
        }

        // Get Current Version
        def currentVersion = runGitSemver(binary, projectDir, "latest", params.getStable().get())
        if (currentVersion == null) {
             return "0.0.0-UNKNOWN"
        }

        // Get Next Version
        def nextVersion = runGitSemver(binary, projectDir, "next", params.getStable().get())
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

        return snapshot ? nextVersion + params.getSnapshotSuffix().get() : nextVersion
    }

    private File install(File location, Params params) {
         def repo = params.getRepository().get()
         def arch = params.getArch().getOrNull()
         def os = params.getOs().getOrNull()
         def version = params.getToolVersion().get()

         try {
             def context = GitSemverInstallation.install(repo, arch, os, version, location)
             return context.binary
         } catch (Exception e) {
             // In case of error, we can't log easily from here without SLF4J or similar
             return null
         }
    }

    private String runGitSemver(File binary, File projectDir, String mode, boolean stable) {
        def args = []
        args.add(mode)
        if (mode == "next") {
             args.add("--stable=" + stable)
        }
        args.add("-w")
        args.add(projectDir.getAbsolutePath())

        return execute(binary.getAbsolutePath(), args, projectDir)
    }

    private boolean checkGitStatus(File projectDir) {
        def output = execute("git", ["status", "--porcelain=v1"], projectDir)
        return output != null && !output.isEmpty()
    }

    private String execute(String command, List<String> args, File workDir) {
        def stdout = new ByteArrayOutputStream()
        def stderr = new ByteArrayOutputStream()
        try {
            def result = getExecOperations().exec {
                workingDir workDir
                commandLine command
                args args
                standardOutput = stdout
                errorOutput = stderr
                ignoreExitValue = true
            }
            if (result.exitValue == 0) {
                return stdout.toString().trim()
            } else {
                return null
            }
        } catch (Exception e) {
            return null
        }
    }
}
