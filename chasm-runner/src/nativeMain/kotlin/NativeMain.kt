package at.released.weh.example.chasm.runner

import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val binaryPath = if (args.isNotEmpty()) args[0] else usage()
    val preopenedDirectory = if (args.size >= 2) args[1] else "."

    // Load WebAssembly binary
    val wasmBinary: ByteArray = SystemFileSystem.source(Path(binaryPath)).buffered().use(Source::readByteArray)

    executeWebAssemblyCode(wasmBinary, preopenedDirectory)
}

private fun usage(): Nothing {
    println("Usage: wasmcoderunner <wasm binary> [preopened directory]")
    exitProcess(1)
}
