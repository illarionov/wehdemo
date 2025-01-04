package at.released.weh.example.chasm.runner

import java.io.InputStream

fun main(args: Array<String>) {
    // Load WebAssembly binary
    val wasmBinary: ByteArray = checkNotNull(
        Thread.currentThread().contextClassLoader.getResource("wehdemo-wasm-code-wasm-wasi.wasm"),
    ).openStream().use(InputStream::readAllBytes)

    val preopenedDirectory = if (args.isNotEmpty()) args[0] else "."

    executeWebAssemblyCode(
        wasmBinary = wasmBinary,
        preopenedDirectoryRealPath = preopenedDirectory,
        debug = false,
    )
}
