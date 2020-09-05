import PrisonersDilemma.Decision.*

class PrisonersDilemma(
    val iterations: Int = 10,
    var penaltyMatrix: Map<Pair<Decision, Decision>, Pair<Double, Double>> = mapOf(
        Pair(COOP, COOP) to Pair(5.0, 5.0),
        Pair(COOP, DEFECT) to Pair(9.0, 3.0),
        Pair(DEFECT, COOP) to Pair(3.0, 9.0),
        Pair(DEFECT, DEFECT) to Pair(7.0, 7.0)
    )
) {
    fun runGame(
        strategyA: StateMachine<Decision, Decision>,
        strategyB: StateMachine<Decision, Decision>
    ): Pair<Double, Double> {
        var accScoreA = 0.0
        var accScoreB = 0.0
        var decisionA: Decision = strategyA.getFirstOutput()
        var decisionB: Decision = strategyB.getFirstOutput()
        for (i in 1..iterations) {
            val (scoreA, scoreB) = penaltyMatrix.getValue(Pair(decisionA, decisionB))
            accScoreA += scoreA
            accScoreB += scoreB
            val tempDecisionA = decisionA
            decisionA = strategyA.getOutput(decisionB)
            decisionB = strategyB.getOutput(tempDecisionA)
        }
        return Pair(accScoreA, accScoreB)
    }

    companion object {
        private val getStateMachine = { initOutput: Decision, nStates: Int ->
            StateMachine(values().toSet(), values().toSet(), initOutput, nStates)
        }

        fun getStrategies(): Set<StateMachine<Decision, Decision>> {
            return setOf(getAlwaysCoop(), getAlwaysDefect(), getTitForTat(), getPunishing(), getForgiving())
        }

        fun getForgiving(): StateMachine<Decision, Decision> {
            TODO("Not yet implemented")
        }

        fun getPunishing(): StateMachine<Decision, Decision> {
            TODO("Not yet implemented")
        }

        fun getTitForTat(): StateMachine<Decision, Decision> {
            TODO("Not yet implemented")
        }

        fun getAlwaysDefect(): StateMachine<Decision, Decision> {
            val strategy = getStateMachine(DEFECT, 1)
            strategy.changeTransitionFunctionEntry(0, 0, COOP, DEFECT)
            strategy.changeTransitionFunctionEntry(0, 0, DEFECT, DEFECT)
            return strategy
        }

        fun getAlwaysCoop(): StateMachine<Decision, Decision> {
            val strategy = getStateMachine(COOP, 1)
            strategy.changeTransitionFunctionEntry(0, 0, COOP, COOP)
            strategy.changeTransitionFunctionEntry(0, 0, DEFECT, COOP)
            return strategy
        }
    }

    enum class Decision {
        COOP, DEFECT
    }
}