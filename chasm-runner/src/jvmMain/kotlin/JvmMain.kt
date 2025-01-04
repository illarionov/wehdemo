package at.released.weh.example.chasm.runner

import java.io.InputStream

fun main() {
    // Load WebAssembly binary
    val wasmBinary: ByteArray = checkNotNull(
        Thread.currentThread().contextClassLoader.getResource("wehdemo-wasm-code-wasm-wasi.wasm"),
    ).openStream().use(InputStream::readAllBytes)

    executeWebAssemblyCode(wasmBinary)
}
