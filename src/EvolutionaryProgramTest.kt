import org.junit.jupiter.api.Test
import junitparams.JUnitParamsRunner
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.runner.RunWith
import PrisonersDilemma.*
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
        val prisonerMock = mockk<Prisoner>()
        val expectedPrisoners = (1..3).map { prisonerMock }.toMutableList()
        val ep = EvolutionaryProgram(prisonersDilemma = PrisonersDilemma(), initialPopulation = expectedPrisoners)
        assertEquals(expectedPrisoners, ep.initialPopulation)
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
                        .map { mockk<Prisoner>() }
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
        val prisoner = Prisoner(COOP, 2)
        (1..25).forEach { _ ->
            val epSpy = spyk(EvolutionaryProgram(
                prisonersDilemma = dilemmaMock,
                initialPopulation = mutableListOf(prisoner)
            ))
            epSpy.evolveGeneration()
            verify { epSpy.mutate(prisoner) }
            val mutatedStrategy = epSpy.mutate(prisoner)
            assertNotEquals(prisoner, mutatedStrategy)
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

    @Test
    fun given_initPopulationList_when_constructing_then_cannotManipulateInitPopulationFromOutside() {
        fail("Not implemented")
    }
}