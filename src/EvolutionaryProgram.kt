import PrisonersDilemma.*
import PrisonersDilemma.Decision.*
import java.lang.IllegalArgumentException

class EvolutionaryProgram(
    val populationSize: Int = 5,
    var childrenPerParent: Int = 1,
    var initialPopulation: MutableList<StateMachine<Decision, Decision>> = mutableListOf(),
    var prisonersDilemma: PrisonersDilemma,
    var maxFstStates: Int = 10
) {
    init {
        if (childrenPerParent < 1) throw IllegalArgumentException("childrenPerParent must be positive")
        if (populationSize < 1) throw IllegalArgumentException("populationSize must be positive")
        initialPopulation = initialPopulation.toMutableList()
    }

    var population = initialPopulation
    val statistics = mutableMapOf<String, Any>(
        "AVERAGE_FITNESS" to mutableListOf<Double>(),
        "HIGHEST_FITNESS" to mutableListOf<Double>(),
        "LOWEST_FITNESS" to mutableListOf<Double>(),
        "AVERAGE_STRATEGY_SIZE" to mutableListOf<Double>()
    )

    fun evolveGeneration() {
        val mutatedPopulation = mutatePop()
        val populationAndFitnessPairs = (population + mutatedPopulation).map { Pair(it, 0.0) }.toMutableList()
        for (i in 0 until populationAndFitnessPairs.size - 1) {
            val (stateMachineA, accFitnessA) = populationAndFitnessPairs[i]
            val strategyA = castToStrategy(stateMachineA)
            for (j in i + 1 until populationAndFitnessPairs.size) {
                val (stateMachineB, accFitnessB) = populationAndFitnessPairs[j]
                val strategyB = castToStrategy(stateMachineB)
                val (fitnessA, fitnessB) = prisonersDilemma.runGame(strategyA, strategyB)
                populationAndFitnessPairs[i] = Pair(stateMachineA, accFitnessA + fitnessA)
                populationAndFitnessPairs[j] = Pair(stateMachineB, accFitnessB + fitnessB)
            }
        }
        // gatherStatistics(populationAndFitnessPairs)
        // population = selectNextGeneration(populationAndFitnessPairs)
    }

    private fun castToStrategy(machine: StateMachine<Decision, Decision>): Strategy {
        return object : Strategy {
            val m = machine
            override fun next(decision: Decision?): Decision {
                return if (decision == null) m.getFirstOutput() else m.next(decision)
            }

        }
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

    internal fun mutate(strategy: StateMachine<Decision, Decision>): StateMachine<Decision, Decision> {
        if (strategy.initOutput == COOP) {
            strategy.initOutput = DEFECT
        } else {
            strategy.initOutput = COOP
        }
        return strategy
    }

    private fun initializePopulation(): MutableList<StateMachine<Decision, Decision>> {
        TODO("Not yet implemented")
    }

    fun mutatePop(): List<StateMachine<Decision, Decision>> {
        val mutated = mutableListOf<StateMachine<Decision, Decision>>()
        (1..childrenPerParent).forEach { _ -> mutated += population.map { mutate(it.copy()) } }
        return mutated
    }

    internal fun selectMutationPop() {
        TODO("Not yet implemented")
    }

    internal fun trimPop() {
        TODO("Not yet implemented")
    }

    companion object {
        fun getStrategyMachines(): StateMachine<Decision, Decision> {
            TODO("Not yet implemented")
        }

    }
}