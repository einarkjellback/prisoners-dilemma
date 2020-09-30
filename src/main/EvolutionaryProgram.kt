package main

import main.PrisonersDilemma.Decision.*

class EvolutionaryProgram(
    val populationSize: Int = 5,
    var childrenPerParent: Int = 1,
    var initialPopulation: MutableList<Prisoner> = mutableListOf(),
    var prisonersDilemma: PrisonersDilemma,
    var maxFstStates: Int = 10
) {
    init {
        require(childrenPerParent >= 1) { "childrenPerParent must be positive" }
        require(populationSize >= 1) { "populationSize must be positive" }
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
            val (prisonerA, accFitnessA) = populationAndFitnessPairs[i]
            for (j in i + 1 until populationAndFitnessPairs.size) {
                val (prisonerB, accFitnessB) = populationAndFitnessPairs[j]
                val (fitnessA, fitnessB) = prisonersDilemma.runGame(prisonerA, prisonerB)
                populationAndFitnessPairs[i] = Pair(prisonerA, accFitnessA + fitnessA)
                populationAndFitnessPairs[j] = Pair(prisonerB, accFitnessB + fitnessB)
            }
        }
        // gatherStatistics(populationAndFitnessPairs)
        // population = selectNextGeneration(populationAndFitnessPairs)
    }

    private fun gatherStatistics(
        populationAndFitnessPairs: MutableList<Pair<Prisoner, Double>>) {
        TODO("Not yet implemented")
    }

    private fun selectNextGeneration(
        populationAndFitnesses: MutableList<Pair<Prisoner, Double>>) : MutableList<Prisoner> {
        TODO("Not yet implemented")
    }

    internal fun mutate(prisoner: Prisoner): Prisoner {
        if (prisoner.initOutput == COOP) {
            prisoner.initOutput = DEFECT
        } else {
            prisoner.initOutput = COOP
        }
        return prisoner
    }

    private fun initializePopulation(): MutableList<Prisoner> {
        TODO("Not yet implemented")
    }

    fun mutatePop(): List<Prisoner> {
        val mutated = mutableListOf<Prisoner>()
//        (1..childrenPerParent).forEach { _ -> mutated += population.map { mutate(main.Prisoner(it)) } }
        return mutated
    }

    internal fun selectMutationPop() {
        TODO("Not yet implemented")
    }

    internal fun trimPop() {
        TODO("Not yet implemented")
    }

    companion object {
        fun getPrisoners(): List<Prisoner> {
            return listOf(
                getAlwaysDefect(),
                getTitForTat(),
                getPunishing(),
                getForgiving()
            )
        }

        fun getForgiving(): Prisoner {
            TODO("Not yet implemented")
        }

        fun getPunishing(): Prisoner {
            TODO("Not yet implemented")
        }

        fun getTitForTat(): Prisoner {
            return Prisoner(COOP, 1).apply {
                changeTransitionFunction(0, 0, COOP, COOP)
                changeTransitionFunction(0, 0, DEFECT, DEFECT)
            }
        }

        fun getAlwaysDefect(): Prisoner {
            TODO("Not yet implemented")
        }
    }
}