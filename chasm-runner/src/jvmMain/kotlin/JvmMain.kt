package at.released.weh.example.chasm.runner

import one.profiler.AsyncProfiler
import java.io.InputStream

fun main(args: Array<String>) {
    // Load WebAssembly binary
    val wasmBinary: ByteArray = checkNotNull(
        Thread.currentThread().contextClassLoader.getResource("wehdemo-wasm-code-wasm-wasi.wasm"),
    ).openStream().use(InputStream::readAllBytes)

    val preopenedDirectory = if (args.isNotEmpty()) {
        args[0]
    } else {
        System.getProperty("weh.preopened", ".")
    }

    val asyncProfiler = AsyncProfiler.getInstance()

    asyncProfiler.execute("start,event=cpu,alloc,file=profile-%p-%t.jfr")
    try {
        executeWebAssemblyCode(
            wasmBinary = wasmBinary,
            preopenedDirectoryRealPath = preopenedDirectory,
            debug = false,
        )
    } finally {
        asyncProfiler.execute("stop")
    }
}
