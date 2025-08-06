plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.nztang"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.java-diff-utils:java-diff-utils:4.15")
    implementation("com.google.code.gson:gson:2.12.1")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
//    localPath.set( // Example paths for local development
//        if (System.getProperty("os.name").lowercase().contains("windows")) {
//            "D:/Program Files/JetBrains/PyCharm 2024.3.4"
//        } else {
//            "/Applications/PyCharm.app/Contents"
//        }
//    )
    version.set("2024.3.4")
    type.set("IC") // use IntelliJ Community base, it supports all IDEs
//    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("242")
        untilBuild.set("252.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
