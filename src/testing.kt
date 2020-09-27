import io.mockk.every
import io.mockk.spyk

fun main() {
    val l = listOf(1, 2, 3, 4)
    println(l.zipWithNext())
}