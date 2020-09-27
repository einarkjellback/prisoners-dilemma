import PrisonersDilemma.Companion.getAlwaysCoop
import PrisonersDilemma.Companion.getAlwaysDefect
import PrisonersDilemma.Companion.getForgiving
import PrisonersDilemma.Companion.getPunishing
import PrisonersDilemma.Companion.getTitForTat
import PrisonersDilemma.Decision
import PrisonersDilemma.Decision.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.random.Random.Default.nextDouble

internal class PrisonersDilemmaTest {

    private val alwaysCoopOutput = List(20) { COOP }
    private val alwaysDefectOutput = List(20) { DEFECT }
    private val randomOutput = List(40) { listOf(COOP, DEFECT).random() }
    private val opponents = listOf(alwaysCoopOutput, alwaysDefectOutput, randomOutput)

    @Test
    fun testTitForTat() {
        opponents.forEach { opponent ->
            val titForTat = getTitForTat()
            assertEquals(COOP, titForTat.next(null))
            opponent.forEach {
                assertEquals(it, titForTat.next(it))
            }
        }
    }

    @Test
    fun testPunishing() {
        opponents.forEach { opponent ->
            val punishing = getPunishing()
            assertEquals(COOP, punishing.next(null))
            var betrayed = false
            opponent.forEach {
                betrayed = it == DEFECT || betrayed
                assertEquals(if (betrayed) DEFECT else COOP, punishing.next(it))
            }
        }
    }

    @Test
    fun testForgiving() {
        opponents.forEach { opponent ->
            val forgiving = getForgiving()
            assertEquals(COOP, forgiving.next(null))
            var betrayed = 0
            opponent.forEach {
                betrayed = if (it == DEFECT) 3 else max(0, betrayed - 1)
                assertEquals(if (betrayed == 0) COOP else DEFECT, forgiving.next(it))
            }
        }
    }

    @Test
    fun when_iterationsEqualsX_then_runGameRunsForXIterations() {
        val iterations = 50
        val dilemma = PrisonersDilemma(iterations, mapOf(
            Pair(COOP, COOP) to Pair(0.0, 0.0),
            Pair(DEFECT, COOP) to Pair(0.0, 1.0),
            Pair(COOP, DEFECT) to Pair(1.0, 0.0),
            Pair(DEFECT, DEFECT) to Pair(0.0, 0.0)
        ))
        val (shouldBeIterations, _) = dilemma.runGame(getAlwaysCoop(), getAlwaysDefect())
        assertEquals(iterations, shouldBeIterations.toInt())
    }

    @Test
    fun when_runGameCalled_given_alwaysCoopAndAlwaysDefect_then_returnsCorrectScores() {
        val iterations = 49
        val dilemma = PrisonersDilemma(iterations, mapOf(
            Pair(DEFECT, DEFECT) to Pair(100.0, 200.0),
            Pair(COOP, COOP) to Pair(1.0, 2.0),
            Pair(COOP, DEFECT) to Pair(0.01, 0.02),
            Pair(DEFECT, COOP) to Pair(0.0001, 0.0002)
        ))

        val (scoreCoop, scoreDefect) = dilemma.runGame(getAlwaysCoop(), getAlwaysDefect())
        assertEquals(0.01 * iterations, scoreCoop, 0.00001)
        assertEquals(0.02 * iterations, scoreDefect, 0.00001)
    }

    @Test
    fun when_runGameCalled_given_titForTatAndRandomStrategy_then_returnsCorrectScores() {
        val iterations = 49

        val dilemma = PrisonersDilemma(iterations, mapOf(
            Pair(DEFECT, DEFECT) to Pair(100.0, 200.0),
            Pair(COOP, COOP) to Pair(1.0, 2.0),
            Pair(COOP, DEFECT) to Pair(0.01, 0.02),
            Pair(DEFECT, COOP) to Pair(0.0001, 0.0002)
        ))

        val randomStrategy = mockk<Strategy>()
        val randomOutput = List(iterations) { listOf(COOP, DEFECT).random() }
        every { randomStrategy.next(null) } returns randomOutput.first()
        every { randomStrategy.next(any()) } returnsMany randomOutput.drop(1)
        val expectedOutputTit = listOf(COOP) + randomOutput.dropLast(1)
        val expectedScores = randomOutput.zip(expectedOutputTit) { r, t -> dilemma.penaltyMatrix.getValue(Pair(r, t)) }
        val expectedScoreTit = expectedScores.map(Pair<Double, Double>::second).sum()

        val expectedScoreRandom = expectedScores.map(Pair<Double, Double>::first).sum()
        val (actualScoreRandom, actualScoreTitForTat) = dilemma.runGame(randomStrategy, getTitForTat())

        assertEquals(expectedScoreRandom, actualScoreRandom, 0.00001)
        assertEquals(expectedScoreTit, actualScoreTitForTat, 0.00001)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, -1, -41])
    fun when_nonPositiveIterations_then_throwIAE(iterations: Int) {
        val penaltyMatrix = mockk<Map<Pair<Decision, Decision>, Pair<Double, Double>>>()
        assertThrows(IllegalArgumentException::class.java) { PrisonersDilemma(iterations, penaltyMatrix) }
    }

    @Test
    fun when_invalidPenaltyMatrixProvided_then_throwIAE() {
        val getRandom = { nextDouble() * 1000 - 500 }
        val getRandomPair = { Pair(getRandom(), getRandom()) }
        val validMatrix = mapOf(
            Pair(COOP, COOP) to getRandomPair(),
            Pair(COOP, DEFECT) to getRandomPair(),
            Pair(DEFECT, COOP) to getRandomPair(),
            Pair(DEFECT, DEFECT) to getRandomPair()
        )
        val removeEntry: (Decision, Decision) -> Map<Pair<Decision, Decision>, Pair<Double, Double>> = {
                decisionA: Decision,
                decisionB: Decision ->
            val matrix = validMatrix.toMutableMap()
            matrix.remove(Pair(decisionA, decisionB))
            matrix.toMap()
        }
        val invalidMatrices = listOf(
            removeEntry(COOP, COOP),
            removeEntry(COOP, DEFECT),
            removeEntry(DEFECT, COOP),
            removeEntry(DEFECT, DEFECT),
            mapOf()
        )
        invalidMatrices.forEach {
            assertThrows(IllegalArgumentException::class.java) { PrisonersDilemma(1, it) }
        }
    }
}