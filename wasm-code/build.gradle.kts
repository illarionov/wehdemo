plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    wasmWasi {
        nodejs()
        binaries.executable()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile>().configureEach {
    compilerOptions.freeCompilerArgs.addAll(listOf("-Xwasm-use-new-exception-proposal"))
}

