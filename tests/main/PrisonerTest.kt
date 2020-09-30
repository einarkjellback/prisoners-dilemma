package main

import main.PrisonersDilemma.Decision.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
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
            changeTransitionFunction(0, 1, COOP, COOP)
            changeTransitionFunction(0, 0, DEFECT, DEFECT)
            changeTransitionFunction(1, 2, COOP, COOP)
            changeTransitionFunction(1, 0, DEFECT, DEFECT)
            changeTransitionFunction(2, 2, COOP, DEFECT)
            changeTransitionFunction(2, 0, DEFECT, DEFECT)
        }
        val inputsAndOutputs = listOf(null to DEFECT, COOP to COOP, COOP to COOP, COOP to DEFECT, COOP to DEFECT,
            DEFECT to DEFECT, DEFECT to DEFECT, COOP to COOP)
        inputsAndOutputs.forEach { (input, expectedOutput) ->
            assertEquals(expectedOutput, prisoner.next(input))
        }
    }
}