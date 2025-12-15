/* (C) 2024-2025 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import org.apache.commons.io.FileUtils
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import java.nio.file.Path

class GitSemverPluginSpec extends Specification {


    @TempDir
    Path tempDir

    
    def "initialise plugin"() {
        setup:
            def root = tempDir.toFile()
            Project project = ProjectBuilder.builder().withProjectDir(root).build()

        when:
            def plugin = new GitSemverPlugin()
            plugin.apply(project)

        then: 
            project.getTasksByName(GitSemverInstallTask.NAME, false) != null
            !project.getTasksByName(GitSemverInstallTask.NAME, false).isEmpty()
            project.getExtensions().findByName(GitSemverPlugin.NAME) != null
    }

    def "run GitSemverCurrentVersionTask"() {
        setup:
            def root = fsFixture.getCurrentPath().toFile()
            FileUtils.copyDirectoryToDirectory(new File(FileUtils.current(), '.git'), root);
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
            FileUtils.copyDirectoryToDirectory(new File(FileUtils.current(), '.git'), root);
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
            FileUtils.copyDirectoryToDirectory(new File(FileUtils.current(), '.git'), root);
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
            FileUtils.copyDirectoryToDirectory(new File(FileUtils.current(), '.git'), root);
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
            FileUtils.copyDirectoryToDirectory(new File(FileUtils.current(), '.git'), root);
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
