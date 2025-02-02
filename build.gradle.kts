plugins {
    java
}

version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allprojects {
    group = "io.sommers.aiintheipaw"
}

tasks.withType<Test> {
    useJUnitPlatform()
}