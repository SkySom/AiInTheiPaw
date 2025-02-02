plugins {
    id("java")
}

group = "io.sommers.aiintheipaw"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Commander"))
    implementation(project(":Core"))

    implementation("com.github.twitch4j:twitch4j:1.20.0")

    implementation(libs.bundles.all)

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}