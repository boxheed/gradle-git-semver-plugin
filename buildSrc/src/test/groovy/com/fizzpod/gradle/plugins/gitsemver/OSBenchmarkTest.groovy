/* (C) 2026 */
/* SPDX-License-Identifier: Apache-2.0 */
package com.fizzpod.gradle.plugins.gitsemver

import spock.lang.Specification

class OSBenchmarkTest extends Specification {
    def "benchmark findByName"() {
        setup:
        def iterations = 1000000

        // Warmup
        for (int i = 0; i < 10000; i++) {
            OS.Family.findByName("darwin ")
            OS.Arch.findByName(" amd64")
        }

        when:
        def startTime = System.nanoTime()
        for (int i = 0; i < iterations; i++) {
            OS.Family.findByName("darwin ")
            OS.Arch.findByName(" amd64")
        }
        def endTime = System.nanoTime()

        then:
        println "Benchmark time for $iterations iterations: ${(endTime - startTime) / 1000000.0} ms"
        true
    }
}
