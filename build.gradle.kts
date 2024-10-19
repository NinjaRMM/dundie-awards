import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.jvm.toolchain.JvmVendorSpec.ADOPTIUM

plugins {
    java
    `jvm-test-suite`
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
