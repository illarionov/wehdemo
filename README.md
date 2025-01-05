# Wasi-emscripten-host example

This example demonstrates running WebAssembly code compiled using Kotlin/Wasm-WASI in a 
Kotlin Multiplatform project that targets the JVM, macOS, and Linux. It uses the [Chasm] WebAssembly code interpreter
and the [Wasi-emscripten-host] implementation of the WASI Preview 1 interfaces.

## Project Structure

The project consists of 2 subprojects:

- `wasm-code`  
   A project with a single `wasmWasi` target, an example of an WASI executable application. In this project, 
   a WebAssembly binary file is compiled, initially intended for execution on NodeJS.
- `chasm-runner`  
   A Kotlin Multiplatform project targeting JVM, macOS and Linux. It runs the WebAssembly binary
   compiled in the `wasm-code` module using the Chasm interpreter.

## Execution

```shell
./gradlew chasm-runner:jvmRun
```

This command runs the code of the `wasm-code` module on the JVM.


```shell
./gradlew chasm-runner:runReleaseExecutable{LinuxX64, MacosArm64, MacosX64, MingwX64}
```

These commands run the native release build on Linux, macOS or Windows.

```shell
./gradlew chasm-runner:runDebugExecutable{LinuxX64, MacosArm64, MacosX64, MingwX64}
```

These commands run the debug build on native platforms.

## Structure of `wasm-code` 

The examples in `wasm-code` are based on two sources:

- MonotonicTime Example  
  From Kotlin/Wasm-WASI examples: [kotlin-wasm-wasi-template]
- Advent of Code 2024 Solutions by @ephemient  
  Compiled for WASI: [https://github.com/ephemient/aoc2024](https://github.com/ephemient/aoc2024) (see [commit 122bde9])

For the second example, a pre-opened directory is mounted into the WebAssembly virtual machine.
This directory contains text files with input data for the puzzles.
By default, a directory with simple stubs (`chasm-runner/preopened_sample`) is used.  
You can specify a custom directory with your input data using the `WASM_DATA` environment variable. For example:

```shell
WASM_DATA="preopened" ./gradlew chasm-runner:jvmRun
```

Relative paths in `WASM_DATA` are resolved from the root of the `chasm-runner` module.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the [BSD 3-Clause License](http://opensource.org/licenses/BSD-3-Clause).

The project uses code from the following repositories as examples:

- https://github.com/Kotlin/kotlin-wasm-wasi-template licensed under the Apache 2 License
- https://github.com/ephemient/aoc2024 with the author's permission, effectively licensed under the BSD 3-Clause License.

[Chasm]: https://github.com/CharlieTap/chasm
[Wasi-emscripten-host]: https://weh.released.at
[kotlin-wasm-wasi-template]: https://github.com/Kotlin/kotlin-wasm-wasi-template/blob/094ba26f4cd8cbed94d050de542ce0edb656aa04/src/wasmWasiMain/kotlin/MonotonicTime.kt
[commit 122bde9]: https://github.com/ephemient/aoc2024/commit/122bde9e77aed9d4087879468d7d8383bb9695f4#diff-d8ba3833d85cbaefe35ca5626212e163d4333e10f3f512392e47e1a970345469R17
