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
    implementation(platform("software.amazon.awssdk:bom:${libs.versions.aws.get()}"))

    implementation(project(":Core"))

    implementation("io.awspring.cloud:spring-cloud-aws-starter:${libs.versions.spring.aws.get()}")

    implementation("software.amazon.awssdk:eventbridge")
    implementation("software.amazon.awssdk:scheduler")
    implementation("software.amazon.awssdk:sts")

    implementation("io.projectreactor:reactor-core")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}