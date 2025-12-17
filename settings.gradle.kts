pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.1"
}

rootProject.name = "ktor-docs"

gitHooks {

    commitMsg {
        conventionalCommits {
            types("feature", "refactoring", "fix", "codestyle", "docs", "revert", "ci", "chore")
        }
    }

    createHooks(overwriteExisting = true)

}
