package main

import main.PrisonersDilemma.*
import main.PrisonersDilemma.Decision.*

class Prisoner(initOutput: Decision, nStates: Int)
    : StateMachine<Decision, Decision>(
        inputSet = values().toSet(),
        outputSet = values().toSet(),
        initOutput = initOutput,
        nStates = nStates
    ), Strategy {

    override fun next(decision: Decision?): Decision {
        return if (decision == null) getFirstOutput() else transition(decision)
    }
}