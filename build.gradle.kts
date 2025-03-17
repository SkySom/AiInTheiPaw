plugins {
    java
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-amazon-services-bom:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-hibernate-reactive")
    implementation("io.quarkus:quarkus-mutiny")
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkiverse.amazonservices:quarkus-amazon-eventbridge")
    implementation("io.quarkus:quarkus-amazon-lambda-rest")
    implementation("io.quarkus:quarkus-logging-json")
    implementation("io.quarkus:quarkus-reactive-pg-client")
    implementation("io.quarkus:quarkus-reactive-routes")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-flyway")

    implementation("org.flywaydb:flyway-database-postgresql")

    testImplementation("io.quarkus:quarkus-junit5")
}

group = "io.sommers.aiintheipaw"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
