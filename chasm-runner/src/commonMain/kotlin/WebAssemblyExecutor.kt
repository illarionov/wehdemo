package at.released.weh.example.chasm.runner

import at.released.weh.bindings.chasm.wasip1.ChasmWasiPreview1Builder
import at.released.weh.common.api.Logger
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

internal fun executeWebAssemblyCode(
    wasmBinary: ByteArray,
    preopenedDirectoryRealPath: String = ".",
    debug: Boolean = false,
) {
    // Setup Host
    EmbedderHost.Builder()
        .apply {
            directories()
                .addPreopenedDirectory(
                    realPath = preopenedDirectoryRealPath,
                    virtualPath = "/data",
                )
            if (debug) {
                rootLogger = PrintlnLogger
            }
        }
        .build()
        .use { embedderHost ->
            // Execute code
            executeWebAssemblyCode(embedderHost, wasmBinary)
        }
}

private fun executeWebAssemblyCode(
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
        .getOrThrow { "Failed to run _initialize: $it" }
}

private fun <S, E : ChasmError> ChasmResult<S, E>.getOrThrow(
    errorMessage: (E) -> String,
): S = when (this) {
    is ChasmResult.Success -> result
    is ChasmResult.Error -> throw WasmException(errorMessage(error))
}

class WasmException(message: String) : RuntimeException(message)

private object PrintlnLogger : Logger {
    override fun v(throwable: Throwable?, message: () -> String) {
        if (throwable != null) {
            println("WEH: ${message()}. Exception: `$throwable`")
        } else {
            println("WEH: ${message()}")
        }
    }
    override fun a(throwable: Throwable?, message: () -> String) = v(throwable, message)
    override fun d(throwable: Throwable?, message: () -> String) = v(throwable, message)
    override fun e(throwable: Throwable?, message: () -> String) = v(throwable, message)
    override fun i(throwable: Throwable?, message: () -> String) = v(throwable, message)
    override fun w(throwable: Throwable?, message: () -> String) = v(throwable, message)
    override fun withTag(tag: String): Logger = this
}
