suspend fun main() {
    println("test")
    try {
        try { error("Test exception") } catch (ise: RuntimeException) { }
        test()
    } catch (ex: Throwable) {
    }
}

suspend fun test() {}
