rootProject.name = "AiInTheiPaw"

include("AWSCore")
include("Commands")
include("Core")
include("EventHandler")
include("LocalRun")
include("Twitch")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("reactor-core", "io.projectreactor:reactor-core:3.6.11")
            library("validation", "jakarta.validation:jakarta.validation-api:3.0.2")
            library("vavr", "io.vavr:vavr:0.10.5")
            library("slf4j", "org.slf4j:slf4j-api:2.0.16")

            library("lambda-core", "com.amazonaws:aws-lambda-java-core:1.2.3")
            library("lambda-events", "com.amazonaws:aws-lambda-java-events:3.14.0")

            library("persistence-api", "jakarta.persistence:jakarta.persistence-api:3.2.0")

            bundle("basics", listOf("reactor-core", "validation", "vavr"))

            version("spring-boot", "3.3.5")
            version("spring-management", "1.1.6")
            version("spring-aws", "3.2.0")
            version("aws", "2.29.45")
        }
        create("testLibs") {
            version("junit", "5.10.0")
        }
    }
}


