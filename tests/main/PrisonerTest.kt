package main

import main.PrisonersDilemma.Decision.*
import org.junit.jupiter.api.Test

class PrisonerTest {
    @Test
    fun given_cloneOfPrisoner_then_cloneIsEqualToOriginal() {
        val prisoner = Prisoner(DEFECT, 3).apply {
            changeTransitionFunction(0, 2, COOP, COOP)
            changeTransitionFunction(0, 0, DEFECT, COOP)
        }
//        assertEquals(prisoner, main.Prisoner(prisoner))
    }
}