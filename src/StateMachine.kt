import java.util.*

/**
 * This class represents a finite state transducer according to it's mathematical definition, which is a six-tuple
 * consisting of an input alphabet, an output alphabet, a set of states (numbered 0 to n), a starting state, a
 * transition function, and an output function.
 */
class StateMachine<I, O>(
    val inputAlphabet: Set<I>,
    val outputAlphabet: Set<O>,
    var initialOutput: O,
    private var nStates: Int,
    private val transitionFunction: List<MutableMap<I, Pair<Int, O>>> = List(nStates) { fromState ->
        inputAlphabet
            .map { Pair(it, Pair(fromState, outputAlphabet.first())) }
            .toMap()
            .toMutableMap()
    }
) {
    private val START_STATE = 0

    fun changeTransitionFunctionEntry(fromState: Int, toState: Int, onInput: I, output: O) {
        TODO("Not yet implemented")
    }

    fun getOutput(input: I): O {
        TODO("Not yet implemented")
    }

    fun getFirstOutput(): O {
        TODO("Not yet implemented")
    }

    fun copy(): StateMachine<I, O> {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        return Objects.hash(inputAlphabet, outputAlphabet, initialOutput, nStates, transitionFunction)
    }

    override fun equals(other: Any?): Boolean {
        return other != null
                && other is StateMachine<*, *>
                && inputAlphabet == other.inputAlphabet
                && outputAlphabet == other.outputAlphabet
                && initialOutput == other.initialOutput
                && transitionFunction == other.transitionFunction
    }
}
