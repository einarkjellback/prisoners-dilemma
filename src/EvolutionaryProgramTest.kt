import org.junit.jupiter.api.Test
import junitparams.JUnitParamsRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.runner.RunWith
import PrisonersDilemma.*
import PrisonersDilemma.Companion.getAlwaysCoop
import PrisonersDilemma.Companion.getAlwaysDefect
import PrisonersDilemma.Companion.getTitForTat
import PrisonersDilemma.Decision.*
import io.mockk.*
import java.lang.IllegalArgumentException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.fail

@RunWith(JUnitParamsRunner::class)
internal class EvolutionaryProgramTest {

    @Test
    fun populationIsInitialPopulationAtGenerationZero() {
        // fail("Not implemented")
        val stateMachineMock = mockk<StateMachine<Decision, Decision>>()
        val expectedStrategies = (1..3).map { stateMachineMock }.toMutableList()
        val ep = EvolutionaryProgram(prisonersDilemma = PrisonersDilemma(), initialPopulation = expectedStrategies)
        assertEquals(expectedStrategies, ep.initialPopulation)
    }

    @Test
    fun gatherStatisticsStoresCorrectNumbers() {
        // Test average fitness

        // Test highest fitness

        // Test lowest fitness
        fail("Not implemented")
    }

    @Test
    fun populationSizeSetToXThenPopulationIsSizeXEachGeneration() {
        val GENS = 20
        val popSize = (Math.random() * 100 + 1).toInt()
        val ep = EvolutionaryProgram(prisonersDilemma = mockk(), populationSize = popSize)
        (1..GENS).forEach { _ ->
            assertEquals(popSize, ep.populationSize)
            ep.evolveGeneration()
        }
    }

    @Test
    fun childrenPerParentSetToXThenXChildrenProducedPerMutation() {
        val dilemmaMock = mockk<PrisonersDilemma>()
        every { dilemmaMock.runGame(any(), any()) } returns Pair(0.0, 0.0)
        (1..5).forEach { populationSize ->
            (1..5).forEach { childrenPerParent ->
                val epSpy = spyk(EvolutionaryProgram(
                    initialPopulation = (1..populationSize)
                        .map { mockk<StateMachine<Decision, Decision>>() }
                        .toMutableList(),
                    prisonersDilemma = dilemmaMock,
                    childrenPerParent = childrenPerParent
                ))
                every { epSpy.mutate(any()) } returnsArgument 0
                epSpy.evolveGeneration()
                verify { epSpy.mutatePop() }
                val mutated = epSpy.mutatePop()
                assertEquals(populationSize * childrenPerParent, mutated.size)
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, -1, -2, -1265])
    fun throwIAEWhenNonPositivePopulationSize(v: Int) {
        assertFailsWith<IllegalArgumentException> {
            EvolutionaryProgram(populationSize = v, prisonersDilemma = mockk())
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, -1, -2, -134])
    fun throwIAEWhenNonPositiveChildrenPerParent(v: Int) {
        assertFailsWith<IllegalArgumentException> {
            EvolutionaryProgram(childrenPerParent = v, prisonersDilemma = mockk())
        }
    }

    @Test
    fun childDifferentFromParentAfterMutation() {
        val dilemmaMock = mockk<PrisonersDilemma>()
        every { dilemmaMock.runGame(any(), any()) } returns Pair(0.0, 0.0)
        val strategy = StateMachine(values().toSet(), values().toSet(), COOP, 2)
        (1..25).forEach { _ ->
            val epSpy = spyk(EvolutionaryProgram(
                prisonersDilemma = dilemmaMock,
                initialPopulation = mutableListOf(strategy)
            ))
            epSpy.evolveGeneration()
            verify { epSpy.mutate(strategy) }
            val mutatedStrategy = epSpy.mutate(strategy)
            assertNotEquals(strategy, mutatedStrategy)
        }
    }

    @Test
    fun testEvolveGenerationFollowsEvolutionaryProgramAlgorithm() {
        val dilemma = mockk<PrisonersDilemma>()
        val ep = spyk(EvolutionaryProgram(prisonersDilemma = dilemma))
        ep.evolveGeneration()
        verifyOrder {
            ep.selectMutationPop()
            ep.mutatePop()
            ep.prisonersDilemma.runGame(any(), any())
            ep.trimPop()
        }
    }
}