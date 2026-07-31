plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
}

dependencies {
    intellijPlatform {
        webstorm("2026.1.2")
        bundledPlugin("JavaScript")
        pluginModule(implementation(project(":core")))
    }
}
