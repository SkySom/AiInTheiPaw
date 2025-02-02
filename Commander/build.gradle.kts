plugins {
    java
}

group = "io.sommers"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Core"))

}

tasks.test {
    useJUnitPlatform()
}