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
    implementation(project(":Commands"))
    implementation(project(":Core"))
    implementation(project(":Twitch"))

    annotationProcessor("org.hibernate.validator:hibernate-validator")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("org.hibernate.orm:hibernate-core")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

}