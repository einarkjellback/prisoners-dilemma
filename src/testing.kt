fun main() {
    val lA = listOf(1, 2, 3)
    val lB = listOf(lA)
    println(System.identityHashCode(lA))
    println(System.identityHashCode(lB))
}