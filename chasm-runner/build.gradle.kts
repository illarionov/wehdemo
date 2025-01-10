@file:Suppress("OPT_IN_USAGE", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
    description = "Gathers WASM binary files from dependencies into a single directory"
    from(configurations.named("wasmBinaryFiles"))
    into(wasmBinaryDir)
}

kotlin {
    jvm {
        mainRun {
            mainClass = "at.released.weh.example.chasm.runner.JvmMainKt"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.chasm)
        }
        jvmMain {
            resources.srcDir(files(wasmBinaryDir).builtBy(aggregateBinariesTask))
        }
    }
}

dependencies {
    add("wasmBinary", project(":wasm-code"))
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs(
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-XX:MaxMetaspaceSize=128M",
        "-Xmx4G",
    )
}
