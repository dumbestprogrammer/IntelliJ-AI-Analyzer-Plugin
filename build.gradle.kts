plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.analyzerPlug"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


intellij {
    version.set("2023.3.3")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf(

        "com.intellij.gradle" // Gradle support
    ))
}

dependencies {


    implementation("org.jetbrains:annotations:24.0.1")
    // Gson (For LLMResponseParser)
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.slf4j:slf4j-api:2.0.9")

    implementation("com.github.javaparser:javaparser-core:3.25.8")

    implementation("org.apache.commons:commons-lang3:3.14.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    runPluginVerifier {
        ideVersions.set(listOf("2023.2", "2023.3", "2024.1"))
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("242.*")
    }


    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }


    buildSearchableOptions {
        enabled = false
    }
}
