@file:Suppress("OPT_IN_USAGE")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm {
        mainRun {
            mainClass.set("at/released/weh/example/chasm/runner/WasmCodeRunnerKt")
        }
    }
    iosSimulatorArm64()
    iosArm64()
    iosX64()
    linuxArm64()
    linuxX64()
    macosArm64()
    macosX64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.chasm)
            implementation(libs.weh.chasm.wasip1)
        }
    }
}
