plugins {
    id("java")
    id("org.springframework.boot") version libs.versions.spring.boot.get()
    id("io.spring.dependency-management") version libs.versions.spring.management.get()
}

group = "io.sommers.aiintheipaw"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Core"))
    implementation(project(":EventHandler"))

    implementation(libs.bundles.basics)

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation(platform("org.junit:junit-bom:${testLibs.versions.junit.get()}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}