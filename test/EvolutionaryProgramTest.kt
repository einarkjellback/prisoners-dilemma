import org.junit.jupiter.api.Test
import PrisonersDilemma.*
import PrisonersDilemma.Companion.getAlwaysCoop
import PrisonersDilemma.Companion.getAlwaysDefect
import PrisonersDilemma.Companion.getTitForTat
import PrisonersDilemma.Decision.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito.*
import kotlin.test.assertEquals

internal class EvolutionaryProgramTest {
    @Test
    fun when_atGenerationZero_then_populationContainsInitialPopGivenToConstructor() {}

    @Test
    fun when_gatherStatisticsCalled_then_returnsCorrectNumbers() {
        // Test average fitness

        // Test highest fitness

        // Test lowest fitness
    }

    @Test
    fun when_populationSizeIsX_then_populationSizeIsXEachGeneration() {}

    @Test
    fun when_stopAtGenerationIsX_then_optimizationRunsForXGenerations() {}

    @Test
    fun when_childrenPerParentIsX_then_XChildrenProducedPerMutation() {}

    @Test
    fun integrationTestsEvolutionaryProgram() {
        (1..100).forEach { _ ->
            val alwaysCoop = getAlwaysCoop()
            val dilemma = mock(PrisonersDilemma::class.java)
            val ep = EvolutionaryProgram(1, 1, mutableListOf(alwaysCoop), dilemma, 1)
            assertEquals(listOf(alwaysCoop), ep.population)
            ep.evolveGeneration()

            val doOppositeCoop = StateMachine(Decision.values().toSet(), Decision.values().toSet(), COOP, 1)
            doOppositeCoop.changeTransitionFunctionEntry(0, 0, COOP, DEFECT)
            doOppositeCoop.changeTransitionFunctionEntry(0, 0, DEFECT, COOP)
            val doOppositeDefect = doOppositeCoop.copy()
            doOppositeDefect.initialOutput = DEFECT
            val titForTatInitOutputDefect = getTitForTat().apply { initialOutput = DEFECT }
            val alwaysDefectInitOutputCoop = getAlwaysDefect().apply { initialOutput = COOP }
            val alwaysCoopInitOutputDefect = getAlwaysCoop().apply { initialOutput = DEFECT }

            assertTrue(ep.population.first() in setOf(
                alwaysCoop,
                alwaysCoopInitOutputDefect,
                getTitForTat(),
                titForTatInitOutputDefect,
                getAlwaysDefect(),
                alwaysDefectInitOutputCoop,
                doOppositeCoop,
                doOppositeDefect
                )
            )
        }
    }

    @Test
    fun when_nonPositivePopulationSize_then_throwIAE() {}

    @Test
    fun when_nonPositiveChildrenPerParent_then_throwIAE() {}

    @Test
    fun when_nonPositiveStopAtGeneration_then_throwIAE() {}

    @Test
    fun when_mutating_then_childDifferentFromParent() {}
}