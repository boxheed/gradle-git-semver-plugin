/* (C) 2024 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import nebula.test.ProjectSpec

public class OsSpec extends ProjectSpec {

    def "find os family by name"() {
        expect:
        OS.Family.findByName(name) == family

        where:
        name        | family
        "windows"   | OS.Family.WINDOWS
        "WINDOWS"   | OS.Family.WINDOWS
        "linux"     | OS.Family.LINUX
        "LINUX"     | OS.Family.LINUX
        "mac"       | OS.Family.MAC
        "MAC"       | OS.Family.MAC
        "darwin"    | OS.Family.MAC
        " darwin"   | OS.Family.MAC
        ""          | null
        "banana"    | null
        null        | null
    }

    def "find arch by name"() {
        expect:
        OS.Arch.findByName(name) == arch

        where:
        name        | arch
        "ARM64"   | OS.Arch.ARM64
        "arm64"   | OS.Arch.ARM64
        "AMD64"   | OS.Arch.AMD64
        "amd64"   | OS.Arch.AMD64
        " amd64"  | OS.Arch.AMD64
        ""        | null
        "banana"  | null
        null      | null
    }

}
