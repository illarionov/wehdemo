import kotlin.wasm.unsafe.UnsafeWasmMemoryApi
import kotlin.wasm.unsafe.withScopedMemoryAllocator

@OptIn(UnsafeWasmMemoryApi::class)
suspend fun main() {
    withScopedMemoryAllocator { }
    try {
        try { error("Test exception") } catch (ise: RuntimeException) { }
        test()
    } catch (ex: Throwable) {
    }
}

suspend fun test() {}
