/* (C) 2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import java.util.regex.Pattern
import spock.lang.Specification

class GitHubClientBenchmarkTest extends Specification {
    def "benchmark regex"() {
        expect:
        def iterations = 100000
        def repo = "https://github.com/fizzpod/gradle-git-semver-plugin.git"

        long start1 = System.nanoTime()
        for (int i = 0; i < iterations; i++) {
            def matcher = repo =~ /https?:\/\/[^\/]+\/([^\/]+\/[^\/]+?)(?:\.git)?$/
            if (matcher) {
                def r = matcher[0][1]
            }
        }
        long end1 = System.nanoTime()

        def pattern = Pattern.compile("https?://[^/]+/([^/]+/[^/]+?)(?:\\.git)?\$")
        long start2 = System.nanoTime()
        for (int i = 0; i < iterations; i++) {
            def matcher = pattern.matcher(repo)
            if (matcher.find()) {
                def r = matcher.group(1)
            }
        }
        long end2 = System.nanoTime()

        println "Unoptimized (Groovy =~ operator): ${(end1 - start1) / 1000000} ms"
        println "Optimized (Pattern.compile): ${(end2 - start2) / 1000000} ms"
    }
}
