import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*

/**
 * This class represents a finite state transducer according to it's mathematical definition, which is a six-tuple
 * consisting of an input alphabet, an output alphabet, a set of states (numbered 0 to n), a starting state, a
 * transition function, and an output function.
 */
open class StateMachine<I, O>(
    val inputSet: Set<I>,
    val outputSet: Set<O>,
    var initOutput: O,
    private var nStates: Int
) : Cloneable {
    init {
        when {
            inputSet.isEmpty() -> {
                throw IllegalArgumentException("Empty input set")
            }
            outputSet.isEmpty() -> {
                throw IllegalArgumentException("Empty output set")
            }
            (initOutput in outputSet).not() -> {
                throw IllegalArgumentException("Initial output not in output set")
            }
            nStates <= 0 -> {
                throw IllegalArgumentException("Number of states must be a positive number")
            }
        }
    }

    private var currentState = 0
    private var calledGetFirstOutputOrNext = false

    /*
     * Usage: transitionFunction[fromState][onInput] gives Pair(toState, output)
     */
    private var transitionFunction: List<MutableMap<I, Pair<Int, O>>> = List(nStates) { fromState ->
        inputSet
            .map {
                val toState = if (fromState == nStates - 1) 0 else fromState + 1
                Pair(it, Pair(toState, outputSet.first()))
            }
            .toMap()
            .toMutableMap()
    }

    private val START_STATE = 0

    //------------------------------------------------------------------------------------------------------------------

    fun changeTransitionFunction(fromState: Int, toState: Int, onInput: I, output: O) {
        require(fromState in 0 until nStates && toState in 0 until nStates) { "State index out of bounds" }
        require(onInput in inputSet) { "onInput outside input set" }
        require(output in outputSet) { "Output outside output set" }
        transitionFunction[fromState][onInput] = Pair(toState, output)
    }

    fun next(input: I): O {
        require(input in inputSet) { "Input outside input set" }
        if (!calledGetFirstOutputOrNext) {
            return getFirstOutput()
        }
        val (nextState, output) = transitionFunction[currentState][input]!!
        currentState = nextState
        return output
    }

    fun getFirstOutput(): O {
        check(!calledGetFirstOutputOrNext) { "Cannot call getFirstOutput more than once or after calling next() method" }
        calledGetFirstOutputOrNext = true
        return initOutput
    }

    override fun clone(): StateMachine<I, O> {
        return StateMachine(inputSet, outputSet, initOutput, nStates).also {
            it.transitionFunction = this.transitionFunction
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(inputSet, outputSet, initOutput, nStates, transitionFunction)
    }

    override fun equals(other: Any?): Boolean {
        return other != null
                && other is StateMachine<*, *>
                && inputSet == other.inputSet
                && outputSet == other.outputSet
                && initOutput == other.initOutput
                && transitionFunction == other.transitionFunction

    }

    fun getAdjacent(current: Int): Set<Int> {
        val neighborhood = mutableSetOf<Int>()
        inputSet.forEach {
            neighborhood.add(transitionFunction[current][it]!!.first)
        }
        return neighborhood
    }
}


