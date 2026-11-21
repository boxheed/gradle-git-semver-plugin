/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import java.nio.file.Path
import org.gradle.process.ExecOperations

/**
 * Inspects a project directory to determine whether it is a valid Git repository
 * that the git-semver binary can operate on.
 */
public class GitRepoStatus {

    public enum State {
        VALID,
        NOT_A_GIT_REPO,
        NO_COMMITS,
        WORKTREE

        String warningMessage(File projectDir) {
            switch (this) {
                case NOT_A_GIT_REPO:
                    return "Project directory ${projectDir} is not a Git repository; defaulting to static version."
                case NO_COMMITS:
                    return "Git repository at ${projectDir} has no commits yet; defaulting to static version."
                case WORKTREE:
                    return "Git worktree detected at ${projectDir}; git-semver does not support worktrees, defaulting to static version."
                default:
                    return ""
            }
        }
    }

    /**
     * Inspects {@code projectDir} using git CLI commands via {@code execOperations}.
     */
    static State inspect(ExecOperations execOperations, File projectDir) {
        if (projectDir == null || !projectDir.exists() || !projectDir.isDirectory()) {
            return State.NOT_A_GIT_REPO
        }

        def insideWorkTree = runGit(execOperations, projectDir, ["rev-parse", "--is-inside-work-tree"])
        if (insideWorkTree.exit != 0 || insideWorkTree.sout != "true") {
            return State.NOT_A_GIT_REPO
        }

        def gitDirRes = runGit(execOperations, projectDir, ["rev-parse", "--git-dir"])
        def commonDirRes = runGit(execOperations, projectDir, ["rev-parse", "--git-common-dir"])
        if (gitDirRes.exit == 0 && commonDirRes.exit == 0 && gitDirRes.sout && commonDirRes.sout) {
            Path gitDirPath = resolveNormalizedPath(projectDir, gitDirRes.sout)
            Path commonDirPath = resolveNormalizedPath(projectDir, commonDirRes.sout)
            if (gitDirPath != commonDirPath) {
                return State.WORKTREE
            }
        }

        def headRes = runGit(execOperations, projectDir, ["rev-parse", "--verify", "HEAD"])
        if (headRes.exit != 0) {
            return State.NO_COMMITS
        }

        return State.VALID
    }

    private static Path resolveNormalizedPath(File projectDir, String pathStr) {
        Path base = projectDir.toPath()
        Path resolved = base.resolve(pathStr).normalize()
        try {
            return resolved.toRealPath()
        } catch (Exception ignored) {
            return resolved.toAbsolutePath().normalize()
        }
    }

    private static Map runGit(ExecOperations execOperations, File dir, List<String> args) {
        try {
            def cmd = ["git"] + args
            def result = Command.runInDir(execOperations, cmd, dir)
            return [exit: result.exit, sout: result.sout?.trim() ?: "", serr: result.serr?.trim() ?: ""]
        } catch (Exception e) {
            Loggy.debug("Failed to run git {}: {}", args, e.message)
            return [exit: -1, sout: "", serr: e.message ?: ""]
        }
    }
}
