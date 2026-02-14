/* (C) 2024-2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import spock.lang.Specification

class GitHubClientSpec extends Specification {

    def "verify GitHubClient static fields"() {
        expect:
        GitHubClient.GITHUB_MEDIA_TYPE == "application/vnd.github+json"
        GitHubClient.okclient != null
    }

    def "verify getRelease returns result for valid repo"() {
        when:
        def result = GitHubClient.getRelease("PSanetra/git-semver", "latest")

        then:
        result != null
        result.tag_name != null
        // Basic check to confirm we got a valid JSON response structure
        result.assets instanceof List
    }
}
