package com.fizzpod.gradle.plugins.gitsemver

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import groovy.json.*
import javax.inject.Inject
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.io.FileUtils
import org.kohsuke.github.*
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.CompressionType

public class GitSemverInstallation {

    public static final String GITSEMVER_INSTALL_DIR = ".git-semver"

    static def install = { String repo, String arch, String os, String version, File location ->
        def params = [
            arch: arch,
            os: os,
            location: location,
            version: version,
            repo: repo
        ]
        def context = [params: params]
        def result = Optional.ofNullable(context)
            .map(x -> GitSemverInstallation.os(x))
            .map(x -> GitSemverInstallation.arch(x))
            .map(x -> GitSemverInstallation.artifact(x))
            .map(x -> GitSemverInstallation.bin(x))
            .map(x -> GitSemverInstallation.download(x))
            .map(x -> x.binary)
            .orElseThrow(() -> new RuntimeException("Unable to download git-semver"))
        return result
    }

    static def download = { x ->
        if(!x.binary.exists()) {
            GitSemverInstallation.downloadAndInstall(x.url, x.binary)
        }
        return x.binary.exists()? x: null
    }

    static def downloadAndInstall = { url, binary ->
        def tmp = new File(binary.getParentFile(), "tmp_" + binary.getName() + ".tgz")
        FileUtils.copyURLToFile(new URL(url), tmp, 120000, 120000)
        def archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP)
        archiver.extract(tmp, binary.getParentFile())
        new File(binary.getParentFile(), 'git-semver').renameTo(binary)
        binary.setExecutable(true)
        tmp.delete()
        return binary
    }

    static def bin = { x ->
        def location = x.params.location
        def version = x.version
        def os = x.os
        def arch = x.arch
        x.binary = GitSemverInstallation.binary(location, version, os, arch)
        x.binary? x: null
    }

    static def binary = {location, version, os, arch ->
        def name = GitSemverInstallation.getBinaryName(version, os, arch)
        return new File(location, name)
    }.memoize()


    static def getBinaryName = {version, os, arch ->
        def osId = os.id
        def archId = arch.id
        def extension = os == OS.Family.WINDOWS? ".exe": "";
        def name = "git-semver_${version}_${osId}_${archId}${extension}"
        return name
    }.memoize()

    static def artifact = { x ->
        x = x + GitSemverInstallation.resolveArtifact(x.params.repo, x.arch, x.os, x.params.version)
    }

    static def resolveArtifact = { String repo, OS.Arch arch, OS.Family os, String version ->
        def artifact = GitHubClient.resolve(repo, arch, os, version)
        return [url: artifact.url, version: artifact.version]
    }.memoize()

    static def os = {def x ->
        x.os = OS.getOs(x.params.os)
        x.os? x: null;
    }.memoize()

    static def arch = {def x ->
        x.arch = OS.getArch(x.params.arch)
        x.arch? x: null;
    }.memoize()
}