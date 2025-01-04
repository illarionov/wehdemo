# Preopened directory

This directory is added to the WebAssembly virtual machine as a preopened directory and is accessible 
at the virtual path `/data`.
It should contain text files — inputs for the puzzles from [Advent of Code 2024](https://adventofcode.com/2024).

These puzzle inputs are unique to each user and cannot be shared. As a placeholder, this directory includes simple
stubs that pass input validation for the solutions. You can replace these placeholders with your own puzzle inputs.
which can be downloaded after registering on the AOC website.

Alternatively, you can specify a custom directory using the `WASM_DATA` environment variable when running 
`chasm-runner` using Gradle.
