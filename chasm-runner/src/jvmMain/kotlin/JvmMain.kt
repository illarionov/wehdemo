package at.released.weh.example.chasm.runner

import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Value.Number.I32
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.store
import java.io.InputStream
import io.github.charlietap.chasm.embedding.shapes.ValueType.Number.I32 as I32Type

fun main() {
    val wasmBinary: ByteArray = checkNotNull(
        Thread.currentThread().contextClassLoader.getResource("wehdemo-wasm-code-wasm-wasi.wasm"),
    ).openStream().use(InputStream::readAllBytes)

    val store: Store = store()

    val imports = listOf(
        Import(
            moduleName = "wasi_snapshot_preview1",
            entityName = "random_get",
            value = function(store, FunctionType(listOf(I32Type, I32Type), listOf(I32Type))) { (buf, size) ->
                listOf(I32(0))
            },
        ),
        Import(
            moduleName = "wasi_snapshot_preview1",
            entityName = "fd_write",
            value = function(
                store,
                FunctionType(listOf(I32Type, I32Type, I32Type, I32Type), listOf(I32Type)),
            ) { (fd, pCiov, cIovCnt, pNum) ->
                listOf(I32(0))
            },
        ),
    )

    module(wasmBinary)
        .flatMap { module -> instance(store, module, imports) }
        .flatMap { instance -> invoke(store, instance, "_initialize") }
        .fold(
            onSuccess = { println("Success $it") },
            onError = { error -> error("Error $error") },
        )
}
