import com.github.ephemient.aoc2024.exe.ephemientAoc2024Main
import kotlin.time.measureTime

suspend fun main() {
    println("Hello from Kotlin via WASI")
    try {
        monotonicTimeSample()
        measureTime {
            ephemientAoc2024Main()
        }.also {
            println("ephemientAoc2024Main() took $it")
        }
    } catch (ex: Throwable) {
        println("\nUncaught exception:\n${ex.stackTraceToString()}")
    }
}

// We need it to run WasmEdge with the _initialize function
@WasmExport
fun dummy() {}
