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
    implementation("io.vavr:vavr:0.10.6")

    //region Quarkus Code
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation("io.quarkus:quarkus-mutiny")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-cache")
    //endregion

    //region HTTP
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkiverse.resteasy-problem:quarkus-resteasy-problem:3.20.0")
    implementation("io.quarkus:quarkus-hibernate-validator")
    //endregion

    //region Database
    implementation("io.quarkus:quarkus-hibernate-reactive")
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-reactive-pg-client")

    implementation("io.quarkus:quarkus-flyway")

    implementation("org.flywaydb:flyway-database-postgresql")
    //endregion

    //region AWS
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-amazon-services-bom:${quarkusPlatformVersion}"))

    implementation("io.quarkiverse.amazonservices:quarkus-amazon-eventbridge")
    implementation("io.quarkus:quarkus-amazon-lambda-rest")
    //endregion

    //region Test
    testImplementation("io.quarkus:quarkus-junit5")
    //endregion
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
