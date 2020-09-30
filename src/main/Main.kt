package main

fun main() {
    val evolutionaryProgram = EvolutionaryProgram(prisonersDilemma = PrisonersDilemma())
    // TODO: Inject some well-known strategies into the initial population
    val knownStrategies = EvolutionaryProgram.getPrisoners()
    evolutionaryProgram.initialPopulation = knownStrategies.toMutableList()
    val GENERATIONS = 100
    (1..GENERATIONS).forEach { _ ->
        evolutionaryProgram.evolveGeneration()
    }
    // TODO: Present statistics from the run and results
    // TODO: Plot average and fittest individual sizes
    // TODO: Plot average, least fit, and fittest scores
}