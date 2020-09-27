import PrisonersDilemma.*

class Prisoner(initOutput: Decision, nStates: Int)
    : StateMachine<Decision, Decision>(
    inputSet = Decision.values().toSet(),
    outputSet = Decision.values().toSet(),
    initOutput = initOutput,
    nStates = nStates)
    , Strategy {
    override fun next(decision: Decision?): Decision {
        TODO("Not yet implemented")
    }
}