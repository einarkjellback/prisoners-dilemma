package main

import java.lang.IllegalArgumentException
import java.util.*

/**
 * This class represents a finite state transducer according to it's mathematical definition, which is a six-tuple
 * consisting of an input alphabet, an output alphabet, a set of states (numbered 0 to n), a starting state, a
 * transition function, and an output function.
 */
open class StateMachine<I, O>(
    inputSet: Set<I>,
    outputSet: Set<O>,
    var initOutput: O,
    private var nStates: Int
) {
    init {
        require(inputSet.isNotEmpty()) { "Empty input set" }
        require(outputSet.isNotEmpty()) { "Empty output set" }
        require(initOutput in outputSet) { "Initial output not in output set" }
        require(nStates > 0) { "Number of states must be a positive number" }
    }

    val inputSet = inputSet.toSet()
    val outputSet = outputSet.toSet()
    private val startState = 0
    private var currentState = startState
    private var calledGetFirstOutputOrNext = false

    // Usage: transitionFunction[fromState][onInput] gives Pair(toState, output)
    private val transitionFunction = List(nStates) { fromState ->
        inputSet
            .map {
                val toState = if (fromState == nStates - 1) 0 else fromState + 1
                Pair(it, Pair(toState, outputSet.first()))
            }
            .toMap()
            .toMutableMap()
    }

    /**
     * Returns a state machine with the same transition function as the argument. The input and output values are taken
     * from the inputSetCopy and outputSetCopy of stateMachine. This constructor is used for generating deep copies,
     * but only returns one if the elements in inputSetCopy and outputSetCopy does not share reference with
     * stateMachine's inputSet and outputSet.
     * @throws IllegalArgumentException if inputSetCopy unequal to original inputSet or outputSetCopy unequal to
     * original outputSet.
     */
    constructor(stateMachine: StateMachine<I, O>, inputSetCopy: Set<I>, outputSetCopy: Set<O>) :
    this(
        inputSetCopy.toSet(),
        outputSetCopy.toSet(),
        outputSetCopy.find { stateMachine.initOutput == it }
            ?: throw IllegalArgumentException("outputSetCopy unequal to original outputSet."),
        stateMachine.nStates) {

        require(stateMachine.inputSet == inputSetCopy) { "inputSetCopy unequal to original inputSet." }
        require(stateMachine.outputSet.containsAll(outputSetCopy)) { "outputSetCopy unequal to original outputSet." }

        (0 until nStates).forEach { fromState ->
            inputSetCopy.forEach {
                val (toState, output) = stateMachine.transitionFunction[fromState].getValue(it)
                changeTransitionFunction(fromState, toState, it, output)
            }
        }
    }

    fun changeTransitionFunction(fromState: Int, toState: Int, onInput: I, output: O) {
        require(fromState in 0 until nStates && toState in 0 until nStates) { "State index out of bounds" }
        require(onInput in inputSet) { "onInput outside input set" }
        require(output in outputSet) { "Output outside output set" }
        transitionFunction[fromState][onInput] = Pair(toState, output)
    }

    fun transition(input: I): O {
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

    fun getAdjacent(atState: Int): Set<Int> {
        val neighborhood = mutableSetOf<Int>()
        inputSet.forEach {
            neighborhood.add(transitionFunction[atState][it]!!.first)
        }
        return neighborhood
    }
}


