import com.github.ephemient.aoc2024.exe.ephemientAoc2024Main

suspend fun main() {
    println("Hello from Kotlin via WASI")
    try {
        monotonicTimeSample()
        ephemientAoc2024Main()
    } catch (ex: Throwable) {
        println("\nUncaught exception:\n${ex.stackTraceToString()}")
    }
}

// We need it to run WasmEdge with the _initialize function
@WasmExport
fun dummy() {}
