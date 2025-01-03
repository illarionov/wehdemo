dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            name = "PixnewsMaven"
            setUrl("https://maven.pixnews.ru")
            mavenContent {
                includeGroup("at.released.weh")
            }
        }
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "wehdemo"
include("wasm-code", "chasm-runner")
