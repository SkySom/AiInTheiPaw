rootProject.name = "AiInTheiPaw"
include("Commander")
include("Core")
include("Twitch")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("reactor-core", "io.projectreactor:reactor-core:3.6.11")
            library("validation", "jakarta.validation:jakarta.validation-api:3.0.2")
            library("vavr", "io.vavr:vavr:0.10.5")
            library("slf4j", "org.slf4j:slf4j-api:2.0.16")

            bundle("basics", listOf("reactor-core", "validation", "vavr"))

            version("spring-boot", "3.3.5")
            version("spring-management", "1.1.6")
        }
        create("testLibs") {
            version("junit", "5.10.0")
        }
    }
}
include("Commands")
