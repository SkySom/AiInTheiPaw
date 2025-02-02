plugins {
    java
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.management.get()
}

group = "io.sommers"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Core"))

    implementation(libs.bundles.basics)
}

tasks.test {
    useJUnitPlatform()
}