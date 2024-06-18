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

import static com.fizzpod.gradle.plugins.gitsemver.GitSemverInstallHelper.*

public class GitSemverInstallTask extends DefaultTask {

    public static final String NAME = "gitSemverInstall"

    public static final String WINDOWS = "windows"
    public static final String LINUX = "linux"
    public static final String MAC = "darwin"
    
    public static final String AMD64 = "amd64"
    public static final String ARM64 = "arm64"

    public static final String GITSEMVER_INSTALL_DIR = ".git-semver"

    private Project project

    @Inject
    public GitSemverInstallTask(Project project) {
        this.project = project
    }

    static register(Project project) {
        project.getLogger().info("Registering task {}", NAME)
        def taskContainer = project.getTasks()

        taskContainer.create([name: NAME,
            type: GitSemverInstallTask,
            dependsOn: [],
            group: GitSemverPlugin.GROUP,
            description: 'Downloads and installs git-semver'])
    }

    @TaskAction
    def runTask() {
        def extension = project[GitSemverPlugin.NAME]
        def context = [:]
        context.logger = project.getLogger()
        context.project = project
        context.extension = extension
        context.os = getOs(context)
        context.arch = getArch(context)
        context.release = getRelease(context)
        context.asset = getAsset(context)
        context.cache = getCacheLocation(context)
        context.cacheBinary = getCacheBinary(context)
        download(context)
        install(context)
    }

    def install(def context) {
        def version = context.release.getName()
        def installFolder = getInstallRoot(context)
        def gitSemverFile = new File(installFolder, context.cacheBinary.getName())
        def versionFile = new File(installFolder, 'git-semver.version')
        def contents = ""
        if(versionFile.exists()) {
            contents = versionFile.getText()
        }
        if(version.equalsIgnoreCase(contents) && gitSemverFile.exists()) {
            return
        }

        FileUtils.copyFile(context.cacheBinary, gitSemverFile)
        gitSemverFile.setExecutable(true)
        versionFile.write(context.release.getName())
    }
    def getInstallRoot(def context) {
        def root = context.project.rootDir
        return new File(root, GITSEMVER_INSTALL_DIR)
    }

    def getCacheBinary(def context) {
        def gitSemverBinaryCacheFileName = getBinaryName(context.release.getName(), context.os, context.arch)
        return new File(context.cache, gitSemverBinaryCacheFileName)
    }

    def getCacheLocation(def context) {
        def root = context.project.rootDir
        def gitSemverInstallRoot = new File(root, GITSEMVER_INSTALL_DIR)
        def release = context.release
        def version = release.getName()

        return new File(gitSemverInstallRoot, ".cache/" + version)
        //def gitSemverFileName = getBinaryName(version, context.os, context.arch)
        //def gitSemverFile = new File(gitSemverInstallLocation, gitSemverFileName)
        //return gitSemverFile
    }

    def getRelease(def context) {
        def extension = context.extension
        GitHub github = GitHub.connectAnonymously();
        GHRepository gitSemverRepository = github.getRepository(extension.repository);
        GHRelease gitSemverRelease = gitSemverRepository.getLatestRelease();
        //match against requeired release
        if(!"latest".equalsIgnoreCase(extension.version)) {
            Iterable<GHRelease> gitSemverReleases = gitSemverRepository.listReleases();
            gitSemverReleases.forEach(release -> {
                if(extension.version.equalsIgnoreCase(release.getName())) {
                    gitSemverRelease = release
                }
            });
        }
        context.logger.info("git-semver version resolved to {}", gitSemverRelease.getName())
        return gitSemverRelease
    }

    def getAsset(def context) {
        def release = context.release
        def os = context.os
        def arch = context.arch
        //Find the appropriate binary asset
        Iterable<GHAsset> assets = release.listAssets();
        //find the appropriate asset
        GHAsset gitSemverAsset = null;
        assets.forEach( asset -> {
            String assetName = asset.getName()
            if(assetName.contains(os) && assetName.contains(arch)) {
                gitSemverAsset = asset;
            }
        })
        if(gitSemverAsset == null) {
            throw new RuntimeException("Unable to find asset for operating system " + os + " and architecture " + arch)
        }
        return gitSemverAsset
    }
    
    def download(def context) {
        def project = context.project
        def extension = context.extension
        def buildDir = project.buildDir
        def asset = context.asset
        def url = asset.getBrowserDownloadUrl()
        context.logger.info("git-semver url resolved to {}", url)
        def gitSemverCacheBinary = context.cacheBinary
        context.logger.info("Writing git-semver to {}", gitSemverCacheBinary)
        if(!gitSemverCacheBinary.exists()){
            FileUtils.copyURLToFile(new URL(url), gitSemverCacheBinary, 120000, 120000)
            def archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP)
            archiver.extract(gitSemverCacheBinary, gitSemverCacheBinary.getParentFile())
            new File(gitSemverCacheBinary.getParentFile(), 'git-semver').renameTo(gitSemverCacheBinary)
            gitSemverCacheBinary.setExecutable(true)
        }
    }

}