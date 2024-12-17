/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import nebula.test.ProjectSpec

public class GitSemverInstallationSpec extends ProjectSpec {

    def "find os family by name"() {
        expect:
        GitSemverInstallation.os([ params: [os:osName]]).os == family


        where:
        osName      | family
        "windows"   | OS.Family.WINDOWS
        "linux"     | OS.Family.LINUX
        "darwin"    | OS.Family.MAC
        "current"   | OS.Family.resolve() //This test is going to be fragile
        ""          | OS.Family.resolve() //This test is going to be fragile
        null        | OS.Family.resolve() //This test is going to be fragile
    }

    def "find arch by name"() {
        expect:
        GitSemverInstallation.arch([ params: [arch:archName]]).arch == architecture


        where:
        archName    | architecture
        "amd64"     | OS.Arch.AMD64
        "arm64"     | OS.Arch.ARM64
        "current"   | OS.Arch.resolve()
        ""          | OS.Arch.resolve()
        null        | OS.Arch.resolve()
    }

    def "find artifact"() {
        expect:
        GitSemverInstallation.artifact([ params: [repo: repoName, version: version], os: osType, arch: archType]).url != null


        where:
        repoName              | osType          | archType      | version  | ghVersion
        "PSanetra/git-semver" | OS.Family.LINUX | OS.Arch.AMD64 | "latest" | "v1.1.1"
    }


}
