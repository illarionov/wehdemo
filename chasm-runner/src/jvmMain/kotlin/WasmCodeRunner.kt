package at.released.weh.example.chasm.runner

import at.released.weh.bindings.chasm.wasip1.ChasmWasiPreview1Builder
import at.released.weh.host.EmbedderHost
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.store
import java.io.InputStream

fun main() {
    // Load WebAssembly binary
    val wasmBinary: ByteArray = checkNotNull(
        Thread.currentThread().contextClassLoader.getResource("wehdemo-wasm-code-wasm-wasi.wasm"),
    ).openStream().use(InputStream::readAllBytes)

    // Setup Host and run code
    EmbedderHost.Builder()
        .apply {
            directories()
                .addPreopenedDirectory(
                    realPath = ".",
                    virtualPath = "/preopen",
                )
        }
        .build()
        .use { embedderHost -> runWebAssemblyCode(embedderHost, wasmBinary) }
}

private fun runWebAssemblyCode(
    embedderHost: EmbedderHost,
    wasmBinary: ByteArray,
) {
    val store: Store = store()

    // Prepare WASI imports
    val wasiImports: List<Import> = ChasmWasiPreview1Builder(store) {
        host = embedderHost
    }.build()

    // Instantiate the WebAssembly module
    val instance: Instance = module(wasmBinary)
        .flatMap { module -> instance(store, module, wasiImports) }
        .getOrThrow { "Can node instantiate WebAssembly binary: $it" }

    // Initialize environment and run main function
    invoke(store, instance, "_initialize")
        .getOrThrow { "Can not initialize WebAssembly environment: $it" }
}

private fun <S, E : ChasmError> ChasmResult<S, E>.getOrThrow(
    errorMessage: (E) -> String,
): S = when (this) {
    is ChasmResult.Success -> result
    is ChasmResult.Error -> throw WasmException(errorMessage(error))
}

class WasmException(message: String) : RuntimeException(message)
