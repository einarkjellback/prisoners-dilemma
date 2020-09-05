import PrisonersDilemma.Decision

class EvolutionaryProgram(
    val populationSize: Int = 5,
    var childrenPerParent: Int = 1,
    var initialPopulation: MutableList<StateMachine<Decision, Decision>> = mutableListOf(),
    var prisonersDilemma: PrisonersDilemma,
    var maxFstStates: Int = 10
) {
    var population = initialPopulation
    val statistics = mutableMapOf<String, Any>(
        "AVERAGE_FITNESS" to mutableListOf<Double>(),
        "HIGHEST_FITNESS" to mutableListOf<Double>(),
        "LOWEST_FITNESS" to mutableListOf<Double>(),
        "AVERAGE_STRATEGY_SIZE" to mutableListOf<Double>()
    )

    fun evolveGeneration() {
        val mutatedPopulation = population.map { mutate(it) }.toMutableList()
        val populationAndFitnessPairs = (population + mutatedPopulation).map { Pair(it, 0.0) }.toMutableList()
        for (i in 0 until populationAndFitnessPairs.size - 1) {
            val (stateMachineA, accFitnessA) = populationAndFitnessPairs[i]
            for (j in i + 1 until populationAndFitnessPairs.size) {
                val (stateMachineB, accFitnessB) = populationAndFitnessPairs[j]
                val (fitnessA, fitnessB) = prisonersDilemma.runGame(stateMachineA, stateMachineB)
                populationAndFitnessPairs[i] = Pair(stateMachineA, accFitnessA + fitnessA)
                populationAndFitnessPairs[j] = Pair(stateMachineB, accFitnessB + fitnessB)
            }
        }
        gatherStatistics(populationAndFitnessPairs)
        population = selectNextGeneration(populationAndFitnessPairs)
    }

    private fun gatherStatistics(
        populationAndFitnessPairs: MutableList<Pair<StateMachine<Decision, Decision>, Double>>) {
        TODO("Not yet implemented")
    }

    private fun selectNextGeneration(
        populationAndFitnesses: MutableList<Pair<StateMachine<Decision, Decision>, Double>>)
            : MutableList<StateMachine<Decision, Decision>> {
        TODO("Not yet implemented")
    }

    private fun mutate(strategy: StateMachine<Decision, Decision>): StateMachine<Decision, Decision> {
        TODO("Not yet implemented")
    }

    private fun initializePopulation(): MutableList<StateMachine<Decision, Decision>> {
        TODO("Not yet implemented")
    }
}