import PrisonersDilemma.*

fun interface Strategy {
    fun next(decision: Decision?): Decision
}