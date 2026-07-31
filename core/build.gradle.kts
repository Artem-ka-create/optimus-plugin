import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform.module")
}

dependencies {
    testImplementation(libs.junit)

    intellijPlatform {
        webstorm("2026.1.2")
        bundledPlugin("JavaScript")
        testFramework(TestFrameworkType.Platform)
    }
}
