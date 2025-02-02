plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.3"
}

version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.google.cloud:spring-cloud-gcp-dependencies:5.8.0"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.hibernate.validator:hibernate-validator")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.cache2k:cache2k-core")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    implementation("com.google.cloud:spring-cloud-gcp-starter")
    implementation("com.google.cloud:spring-cloud-gcp-starter-data-firestore")

    implementation("org.hibernate.validator:hibernate-validator")
    implementation("com.discord4j:discord4j-core:3.2.5")
    implementation("com.github.twitch4j:twitch4j:1.20.0")
    implementation("org.cache2k:cache2k-api")
    implementation("io.vavr:vavr:0.10.5")

}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:1.2.4")
    }
}

allprojects {
    group = "io.sommers.aiintheipaw"
}

tasks.withType<Test> {
    useJUnitPlatform()
}