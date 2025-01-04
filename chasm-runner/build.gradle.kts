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
            mainClass.set("at/released/weh/example/chasm/runner/JvmMainKt")
        }
    }
    iosSimulatorArm64()
    iosArm64()
    iosX64()
    linuxArm64()
    linuxX64()
    macosArm64()
    macosX64()

//    mingw is not available with chasm 0.9.3, but it may work with 0.9.2
//    mingwX64 {
//        binaries.all { linkerOpts("-lntdll") }
//    }

    targets.withType<KotlinNativeTarget> {
        binaries.executable {
            entryPoint = "at.released.weh.example.chasm.runner.main"
            runTask?.argumentProviders?.add(
                NativeExecutableArgumentProvider(
                    wasmBinary = wasmBinaryDir.map { it.file("wehdemo-wasm-code-wasm-wasi.wasm") },
                    preopenedDirectory = provider { layout.projectDirectory }
                )
            )
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.chasm)
            implementation(libs.weh.chasm.wasip1)
        }
        jvmMain {
            resources.srcDir(aggregateBinariesTask)
        }
        nativeMain.dependencies {
            // TODO: remove?
            implementation(libs.kotlinx.io)
        }
    }
}

dependencies {
    add("wasmBinary", project(":wasm-code"))
}

private class NativeExecutableArgumentProvider(
    private val wasmBinary: Provider<RegularFile>,
    private val preopenedDirectory: Provider<Directory>
) : CommandLineArgumentProvider {
    override fun asArguments(): List<String> = listOf(
        wasmBinary.get().asFile.absolutePath,
        preopenedDirectory.get().asFile.absolutePath,
    )
}
