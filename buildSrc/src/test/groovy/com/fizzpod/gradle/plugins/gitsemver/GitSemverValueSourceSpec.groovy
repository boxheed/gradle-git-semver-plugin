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
}
