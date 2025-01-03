@file:Suppress("OPT_IN_USAGE", "UnstableApiUsage")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

// Configurations used to import WASM binary files from the wasm-code module
configurations {
    val wasmBinaries = dependencyScope("wasmBinary")
    // Resolvable configuration used to import generated WASM binary
    resolvable("wasmBinaryFiles") {
        extendsFrom(wasmBinaries.get())
        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named("wasm-runtime"))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named("library"))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("wasm"))
        }
    }
}

val wasmBinaryDir = layout.buildDirectory.dir("wasmBinary")
val aggregateBinariesTask = tasks.register<Sync>("copyWasmBinaries") {
    from(configurations.named("wasmBinaryFiles"))
    into(wasmBinaryDir)
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
        jvmMain {
            resources.srcDir(aggregateBinariesTask)
        }
    }
}

dependencies {
    add("wasmBinary", project(":wasm-code"))
}



