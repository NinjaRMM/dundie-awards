import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.jvm.toolchain.JvmVendorSpec.ADOPTIUM
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    `jvm-test-suite`
    id("org.springframework.boot") version libs.versions.springBootVersion
}

group = "com.ninjaone"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(VERSION_17.majorVersion)
        vendor = ADOPTIUM
    }
}

testing {
    suites {
        // Unstable, but present since Gradle 7.3, to become standard in 9.x
        @Suppress("UnstableApiUsage")
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(libs.spring.boot.starter.jpa)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.web)
    developmentOnly(libs.spring.boot.devtools)
    runtimeOnly(libs.h2.core)
    // added for Language helper
    // docs say it only needs to be compileOnly, but wasn't working, investigate further
    implementation(libs.jetbrains.annotations)


    testImplementation(libs.spring.boot.starter.test)
}
