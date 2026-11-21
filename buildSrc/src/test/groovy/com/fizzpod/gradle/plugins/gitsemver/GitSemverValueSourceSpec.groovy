/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import java.nio.file.Path
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.TempDir

class GitSemverValueSourceSpec extends Specification {

    @TempDir
    Path tempDir

    def "verify computedVersion provider returns a version"() {
        setup:
            def root = tempDir.toFile()

            // Initialize a git repo
            Command.runInDir("git init", root)
            Command.runInDir("git config user.email \"you@example.com\"", root)
            Command.runInDir("git config user.name \"Your Name\"", root)
            Command.runInDir("git commit --allow-empty -m \"initial\"", root)

            Project project = ProjectBuilder.builder().withProjectDir(root).build()

        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def extension = project.extensions.getByName(GitSemverPlugin.NAME)

            // We need to trigger the provider
            def version = extension.computedVersion.get()

        then:
            version != null
            println "Computed version: $version"
            version.length() > 0
    }

    def "defaults to static version when not in a git repository"() {
        setup:
            def root = tempDir.toFile()
            Project project = ProjectBuilder.builder().withProjectDir(root).build()

        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def extension = project.extensions.getByName(GitSemverPlugin.NAME)
            def version = extension.computedVersion.get()

        then:
            version == "0.0.0"
    }

    def "uses configured defaultVersion when not in a git repository"() {
        setup:
            def root = tempDir.toFile()
            Project project = ProjectBuilder.builder().withProjectDir(root).build()

        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def extension = project.extensions.getByName(GitSemverPlugin.NAME)
            extension.defaultVersion.set("1.0.0-static")
            def version = extension.computedVersion.get()

        then:
            version == "1.0.0-static"
    }

    def "defaults to static version when git repository has no commits"() {
        setup:
            def root = tempDir.toFile()
            Command.runInDir("git init", root)
            Project project = ProjectBuilder.builder().withProjectDir(root).build()

        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def extension = project.extensions.getByName(GitSemverPlugin.NAME)
            def version = extension.computedVersion.get()

        then:
            version == "0.0.0"
    }

    def "defaults to static version when in a git worktree"() {
        setup:
            def mainRepo = tempDir.resolve("main-repo").toFile()
            mainRepo.mkdirs()
            def worktreeDir = tempDir.resolve("worktree").toFile()
            Command.runInDir("git init", mainRepo)
            Command.runInDir("git config user.email \"you@example.com\"", mainRepo)
            Command.runInDir("git config user.name \"Your Name\"", mainRepo)
            Command.runInDir("git commit --allow-empty -m \"initial\"", mainRepo)
            Command.runInDir("git worktree add ${worktreeDir.getAbsolutePath()}", mainRepo)
            Project project = ProjectBuilder.builder().withProjectDir(worktreeDir).build()

        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def extension = project.extensions.getByName(GitSemverPlugin.NAME)
            def version = extension.computedVersion.get()

        then:
            version == "0.0.0"
    }
}
