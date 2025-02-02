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

            bundle("basics", listOf("reactor-core", "validation", "vavr"))
        }
    }
}
