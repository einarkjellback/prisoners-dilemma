package main

import main.PrisonersDilemma.Decision.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrisonerTest {
    @Test
    fun given_initOutput_then_nextReturnsInitOutputFirst() {
        val expectedOutput = COOP
        val prisoner = Prisoner(expectedOutput, 1)
        assertEquals(expectedOutput, prisoner.next(null))
    }

    @Test
    fun given_prisoner_then_nextReturnsCorrect() {
        val prisoner = Prisoner(DEFECT, 3).apply {
            changeTransitionFunction(0, COOP, 1, COOP)
            changeTransitionFunction(0, DEFECT, 0, DEFECT)
            changeTransitionFunction(1, COOP, 2, COOP)
            changeTransitionFunction(1, DEFECT, 0, DEFECT)
            changeTransitionFunction(2, COOP, 2, DEFECT)
            changeTransitionFunction(2, DEFECT, 0, DEFECT)
        }
        val inputsAndOutputs = listOf(null to DEFECT, COOP to COOP, COOP to COOP, COOP to DEFECT, COOP to DEFECT,
            DEFECT to DEFECT, DEFECT to DEFECT, COOP to COOP)
        inputsAndOutputs.forEach { (input, expectedOutput) ->
            assertEquals(expectedOutput, prisoner.next(input))
        }
    }
}