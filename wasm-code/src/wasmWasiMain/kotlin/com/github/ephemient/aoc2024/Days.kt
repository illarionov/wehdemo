package com.github.ephemient.aoc2024

val days: List<Day> = listOf(
    Day(day = 1, ::Day1, Day1::part1, Day1::part2, name = "1"),
    Day(day = 2, ::Day2, Day2::part1, Day2::part2, name = "2"),
    Day(day = 3, ::Day3, Day3::part1, Day3::part2, name = "3"),
    Day(day = 4, ::Day4, Day4::part1, Day4::part2, name = "4"),
    Day(day = 5, ::Day5, Day5::part1, Day5::part2, name = "5"),
    Day(day = 6, ::Day6, Day6::part1, Day6::part2, name = "6"),
    Day(day = 7, ::Day7, Day7::part1, Day7::part2, name = "7"),
)

data class Day(
    val day: Int,
    val parts: Int,
    val solver: (String) -> (List<suspend () -> Any?>),
    val name: String = day.toString(),
)

fun <T> Day(
    day: Int,
    create: (String) -> T,
    vararg parts: suspend (T) -> Any?,
    name: String = day.toString(),
): Day {
    val size = parts.size
    return Day(
        day = day,
        parts = size,
        solver = solver@{
            val solver = try {
                create(it)
            } catch (_: AssertionError) {
                return@solver List(size) { { "SKIPPED" } }
            }
            parts.map { suspend { it.invoke(solver) } }
        },
        name = name,
    )
}
