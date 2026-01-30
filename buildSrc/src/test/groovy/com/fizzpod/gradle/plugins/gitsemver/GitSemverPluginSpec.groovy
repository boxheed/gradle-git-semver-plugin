/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import org.apache.commons.io.FileUtils
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.*
import spock.util.io.*

class GitSemverPluginSpec extends Specification {


    @TempDir
    FileSystemFixture fsFixture

    
    def "initialise plugin"() {
        setup:
            def root = fsFixture.getCurrentPath().toFile()
            Project project = ProjectBuilder.builder().withProjectDir(root).build()

        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)

        then: 
            project.getTasksByName(GitSemverInstallTask.NAME, false) != null
            !project.getTasksByName(GitSemverInstallTask.NAME, false).isEmpty()
            project.getExtensions().findByName(GitSemverPlugin.NAME) != null
    }

    def "verify extension properties"() {
        setup:
            def root = fsFixture.getCurrentPath().toFile()
            Project project = ProjectBuilder.builder().withProjectDir(root).build()

        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def extension = project.extensions.getByName(GitSemverPlugin.NAME)

        then:
            extension.version instanceof org.gradle.api.provider.Property
            extension.version.get() == "latest"

            extension.location instanceof org.gradle.api.provider.Property
            extension.location.get() == ".git-semver"

            extension.repository instanceof org.gradle.api.provider.Property
            extension.repository.get() == "PSanetra/git-semver"

            extension.prefix instanceof org.gradle.api.provider.Property
            extension.prefix.get() == "v"

            extension.flags instanceof org.gradle.api.provider.Property
            extension.flags.get() == ""

            extension.snapshotSuffix instanceof org.gradle.api.provider.Property
            extension.snapshotSuffix.get() == "-SNAPSHOT"

            extension.ttl instanceof org.gradle.api.provider.Property
            extension.ttl.get() == 1000 * 60 * 60 * 24L

            extension.stable instanceof org.gradle.api.provider.Property
            extension.stable.get() == true

            extension.binary instanceof org.gradle.api.provider.Property
            !extension.binary.isPresent()
    }

    def "run GitSemverCurrentVersionTask"() {
        setup:
            def root = fsFixture.getCurrentPath().toFile()
            Command.runInDir("git init", root)
            Command.runInDir("git config user.email \"you@example.com\"", root)
            Command.runInDir("git config user.name \"Your Name\"", root)
            Command.runInDir("git commit --allow-empty -m \"initial\"", root)
            Project project = ProjectBuilder.builder().withProjectDir(root).build()
            
        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def task = project.getTasksByName(GitSemverCurrentVersionTask.NAME, false).iterator().next()
            task.runTask()
        then: 
            //TODO proper assertion
            !project.getTasksByName(GitSemverCurrentVersionTask.NAME, false).isEmpty()
    }

    def "run GitSemverInstallAllTask"() {
        setup:
            def root = fsFixture.getCurrentPath().toFile()
            Command.runInDir("git init", root)
            Command.runInDir("git config user.email \"you@example.com\"", root)
            Command.runInDir("git config user.name \"Your Name\"", root)
            Command.runInDir("git commit --allow-empty -m \"initial\"", root)
            Project project = ProjectBuilder.builder().withProjectDir(root).build()
            
        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def task = project.getTasksByName(GitSemverInstallAllTask.NAME, false).iterator().next()
            task.runTask()
        then: 
            //TODO proper assertion
            !project.getTasksByName(GitSemverInstallAllTask.NAME, false).isEmpty()
    }

    def "run GitSemverInstallTask"() {
        setup:
            def root = fsFixture.getCurrentPath().toFile()
            Command.runInDir("git init", root)
            Command.runInDir("git config user.email \"you@example.com\"", root)
            Command.runInDir("git config user.name \"Your Name\"", root)
            Command.runInDir("git commit --allow-empty -m \"initial\"", root)
            Project project = ProjectBuilder.builder().withProjectDir(root).build()
            
        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def task = project.getTasksByName(GitSemverInstallTask.NAME, false).iterator().next()
            task.runTask()
        then: 
            //TODO proper assertion
            !project.getTasksByName(GitSemverInstallTask.NAME, false).isEmpty()
    }

    def "run GitSemverNextVersionTask"() {
        setup:
            def root = fsFixture.getCurrentPath().toFile()
            Command.runInDir("git init", root)
            Command.runInDir("git config user.email \"you@example.com\"", root)
            Command.runInDir("git config user.name \"Your Name\"", root)
            Command.runInDir("git commit --allow-empty -m \"initial\"", root)
            Project project = ProjectBuilder.builder().withProjectDir(root).build()
            
        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def task = project.getTasksByName(GitSemverNextVersionTask.NAME, false).iterator().next()
            task.runTask()
        then: 
            //TODO proper assertion
            !project.getTasksByName(GitSemverNextVersionTask.NAME, false).isEmpty()
    }

    def "run GitSemverStatusTask"() {
        setup:
            def root = fsFixture.getCurrentPath().toFile()
            Command.runInDir("git init", root)
            Command.runInDir("git config user.email \"you@example.com\"", root)
            Command.runInDir("git config user.name \"Your Name\"", root)
            Command.runInDir("git commit --allow-empty -m \"initial\"", root)
            Project project = ProjectBuilder.builder().withProjectDir(root).build()
            
        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def task = project.getTasksByName(GitSemverStatusTask.NAME, false).iterator().next()
            task.runTask()
        then: 
            //TODO proper assertion
            !project.getTasksByName(GitSemverStatusTask.NAME, false).isEmpty()
    }

    def "run GitSemverTagTask"() {
        setup:
            fsFixture.create {
                dir("repo") {
                    file("README.md")
                }
            }
            def root = fsFixture.resolve("repo").toFile()
            Project project = ProjectBuilder.builder().withProjectDir(root).build()
            def res = Command.runInDir("git init", root)
            println(res)
            res = Command.runInDir("git config user.email \"you@example.com\"", root)
            println(res)
            res = Command.runInDir("git config user.name \"Your Name\"", root)
            println(res)
            res = Command.runInDir("git add -A", root)
            println(res)
            res = Command.runInDir("git commit -m \"initial\"", root)
            println(res)
            res = Command.runInDir("ls -altr", root)
        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)
            def task = project.getTasksByName(GitSemverTagTask.NAME, false).iterator().next()
            task.runTask()
        then: 
            //TODO proper assertion
            !project.getTasksByName(GitSemverTagTask.NAME, false).isEmpty()
    }

}
