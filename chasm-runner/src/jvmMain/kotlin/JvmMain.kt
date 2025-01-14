package at.released.weh.example.chasm.runner

import io.github.charlietap.chasm.embedding.dsl.functionImport
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32
import java.io.InputStream

fun main() {
    val wasmBinary: ByteArray = checkNotNull(
        Thread.currentThread().contextClassLoader.getResource("wehdemo-wasm-code-wasm-wasi.wasm"),
    ).openStream().use(InputStream::readAllBytes)

    val store: Store = store()

    val imports = listOf(
        functionImport(store) {
            moduleName = "wasi_snapshot_preview1"
            entityName = "random_get"
            type {
                params { i32(); i32() }
                results { i32() }
            }
            reference { (buf, size) -> listOf(I32(0)) }
        },
    )

    module(wasmBinary)
        .flatMap { module -> instance(store, module, imports) }
        .flatMap { instance -> invoke(store, instance, "_initialize") }
        .fold(
            onSuccess = { println("Success $it") },
            onError = { error -> error("Error $error") },
        )
}
