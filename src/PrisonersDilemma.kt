import PrisonersDilemma.Decision.*
import kotlin.math.max


class PrisonersDilemma(
    val iterations: Int = 10,
    var penaltyMatrix: Map<Pair<Decision, Decision>, Pair<Double, Double>> = mapOf(
        Pair(COOP, COOP) to Pair(5.0, 5.0),
        Pair(COOP, DEFECT) to Pair(9.0, 3.0),
        Pair(DEFECT, COOP) to Pair(3.0, 9.0),
        Pair(DEFECT, DEFECT) to Pair(7.0, 7.0)
    )
) {
    init {
        require(iterations > 0) { "Non-positive number of iterations" }
        val requiredKeys = listOf(Pair(COOP, COOP), Pair(COOP, DEFECT), Pair(DEFECT, COOP), Pair(DEFECT, DEFECT))
        requiredKeys.forEach {
            require(penaltyMatrix.containsKey(it)) { "penaltyMatrix does not contain key $it" }
        }
    }

    fun runGame(
        strategyA: Strategy,
        strategyB: Strategy
    ): Pair<Double, Double> {
        var decisionA = strategyA.next(null)
        var decisionB = strategyB.next(null)
        val (scoreA_, scoreB_) = penaltyMatrix.getValue(Pair(decisionA, decisionB))
        var accScoreA = scoreA_
        var accScoreB = scoreB_
        for (i in 2..iterations) {
            val tempDecisionA = decisionA
            decisionA = strategyA.next(decisionB)
            decisionB = strategyB.next(tempDecisionA)
            val (scoreA, scoreB) = penaltyMatrix.getValue(Pair(decisionA, decisionB))
            accScoreA += scoreA
            accScoreB += scoreB
        }
        return Pair(accScoreA, accScoreB)
    }

    companion object {

        fun getStrategies(): Set<Strategy> {
            return setOf(getAlwaysCoop(), getAlwaysDefect(), getTitForTat(), getPunishing(), getForgiving())
        }

        fun getForgiving(): Strategy {
            return object : Strategy {
                var betrayed = 0

                override fun next(decision: Decision?): Decision {
                    betrayed = when (decision) {
                        null -> return COOP
                        DEFECT -> 3
                        else -> max(0, betrayed - 1)
                    }
                    return if (betrayed > 0) DEFECT else COOP
                }
            }
        }

        fun getPunishing(): Strategy {
            return object : Strategy {
                var betrayed = false

                override fun next(decision: Decision?): Decision {
                    return if (decision == null) {
                        COOP
                    } else {
                        betrayed = betrayed || decision == DEFECT
                        if (betrayed) DEFECT else COOP
                    }
                }
            }
        }

        fun getTitForTat(): Strategy {
            return Strategy { it ?: COOP }
        }

        fun getAlwaysDefect(): Strategy {
            return Strategy { DEFECT }
        }

        fun getAlwaysCoop(): Strategy {
            return Strategy { COOP }
        }
    }

    enum class Decision {
        COOP, DEFECT
    }
}