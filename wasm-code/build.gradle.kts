@file:Suppress("OPT_IN_USAGE", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode.DEVELOPMENT
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode.PRODUCTION

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

/**
 * Consumable configuration to provide the generated WASM binary artifacts to the chasm-runner module.
 */
val wasmBinaryConfiguration = configurations.consumable("wehdemoWasmBinaryElements") {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named("wasm-runtime"))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named("library"))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("wasm"))
    }
}

kotlin {
    wasmWasi {
        nodejs()
        binaries.executable().let { wasmBinaries ->
            // Add generated WASM binary as outgoing artifact
            wasmBinaryConfiguration.get().outgoing {
                wasmBinaries.filter { it.mode == DEVELOPMENT }.forEach { binary ->
                    val wasmFilename = binary.mainFileName.map { it.replaceAfterLast(".", "wasm") }
                    val wasmFile = binary.linkTask.flatMap { it.destinationDirectory.file(wasmFilename) }
                    artifact(wasmFile)
                }
            }
        }
    }
}

rootProject.configure<BinaryenRootExtension> {
    version = "121"
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile>().configureEach {
    compilerOptions.freeCompilerArgs.addAll(listOf("-Xwasm-use-new-exception-proposal"))
}

