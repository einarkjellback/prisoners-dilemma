package main

import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

internal class StateMachineTest {

    private fun <T, R> makeMachine(
        inputSet: Set<T>,
        outputSet: Set<R>,
        nStates: Int
    ): StateMachine<T, R> {
        return StateMachine(inputSet, outputSet, outputSet.first(), nStates).apply {
            inputSet.zip(outputSet).forEach {
                val (input, output) = it
                (0 until nStates).toMutableList().apply { add(0) }.zipWithNext().forEach { (from, to) ->
                    this.changeTransitionFunction(from, to, input, output)
                }
            }
        }
    }

    private val defaultInput = (1..3).toSet()
    private val defaultOutput = ('a'..'c').toSet()
    private val defaultStates = 2

    private val unequalMachines = listOf(
        makeMachine(defaultInput, defaultOutput, defaultStates),  // Original machine
        makeMachine(defaultOutput, defaultOutput, defaultStates),  // Changed type of input set
        makeMachine(defaultInput, defaultInput, defaultStates),  // Changed type of output set
        makeMachine((2..4).toSet(), defaultOutput, defaultStates),  // Shifted input set
        makeMachine(defaultInput, ('b'..'d').toSet(), defaultStates),  // Shifted output set
        makeMachine((1..2).toSet(), defaultOutput, defaultStates),  // Shortened input set
        makeMachine(defaultInput, ('a'..'b').toSet(), defaultStates),  // Shortened output set
        makeMachine((1..4).toSet(), defaultOutput, defaultStates),  // Extended input set
        makeMachine(defaultInput, ('a'..'d').toSet(), defaultStates),  // Extended output set
        makeMachine(defaultInput, defaultOutput, defaultStates).apply { initOutput = 'b' },  // Changed init output
        makeMachine(defaultInput, defaultOutput, 1),  // Decreased number of states
        makeMachine(defaultInput, defaultOutput, 3),  // Increased number of states
        makeMachine(defaultInput, defaultOutput, defaultStates).apply {
            changeTransitionFunction(1, 0, 1, 'b')
        }  // Changed transition function entry
    )

    @Test
    fun given_emptyInputAlphabet_then_throwIAException() {
        assertThrows(IllegalArgumentException::class.java) {
            StateMachine(setOf<Int>(), setOf(1), 1, 1)
        }
    }

    @Test
    fun given_emptyOutputAlphabet_then_throwIAException() {
        assertThrows(IllegalArgumentException::class.java) {
            StateMachine(setOf(1), setOf(), 1, 1)
        }
    }

    @Test
    fun given_initialOutputNotInOutputAlphabet_then_throwIAException() {
        assertThrows(IllegalArgumentException::class.java) {
            StateMachine(setOf(1), setOf(1), 2, 1)
        }
    }

    @Test
    fun given_getFirstOutput_then_returnInitialOutput() {
        val initOutput = 1
        val machine = StateMachine(setOf(1), setOf(1, 2, 3), initOutput, 1)
        assertEquals(initOutput, machine.getFirstOutput())
    }

    @Test
    fun given_getInitialOutputCalledTwiceOrAfterNext_then_throwRuntimeException() {
        val getMachine = { StateMachine(setOf(1), setOf(1), 1, 1) }
        assertThrows(IllegalStateException::class.java) {
            val machine = getMachine()
            machine.getFirstOutput()
            machine.getFirstOutput()
        }
        assertThrows(IllegalStateException::class.java) {
            val machine = getMachine()
            machine.next(1)
            machine.getFirstOutput()
        }
    }

    @Test
    fun given_changeTransitionFunctionEntry_when_fromStateOrToStateOutsideBounds_then_throwIAException() {
        val nStates = 3
        val machine = StateMachine(setOf(1), setOf(1), 1, nStates)
        val cases = listOf(
            { machine.changeTransitionFunction(0, nStates, 1, 1) },
            { machine.changeTransitionFunction(nStates, 0, 1, 1) },
            { machine.changeTransitionFunction(-1, 0, 1, 1) },
            { machine.changeTransitionFunction(0, -1, 1, 1) }
        )
        cases.map {
            assertThrows(IllegalArgumentException::class.java) { it() }
        }

    }

    @Test
    fun given_nonPositiveNumberOfStates_then_throwIAException() {
        assertThrows(IllegalArgumentException::class.java) {
            StateMachine(setOf(1), setOf(1), 1, 0)
        }
    }

    @Test
    fun given_inputNotInInputSet_when_nextCalled_then_throwIAException() {
        val machine = StateMachine(setOf(1), setOf(1), 1, 1)
        assertThrows(IllegalArgumentException::class.java) {
            machine.next(0)
        }
    }

    @Test
    fun given_inputNotInInputSet_when_changeTransitionFunctionCalled_then_throwIAException() {
        val machine = StateMachine(setOf(1), setOf(1), 1, 1)
        assertThrows(IllegalArgumentException::class.java) {
            machine.changeTransitionFunction(0, 0, 0, 1)
        }
    }

    @Test
    fun given_changeTransitionFunction_then_transitionFunctionUpdatesCorrectly() {
        val getMachine = {
            StateMachine(setOf(1, 2), setOf(3, 4), 3, 3).apply {
                changeTransitionFunction(0, 0, 1, 3)
                changeTransitionFunction(0, 1, 2, 4)
                changeTransitionFunction(1, 2, 1, 4)
                changeTransitionFunction(1, 0, 2, 4)
                changeTransitionFunction(2, 0, 1, 3)
                changeTransitionFunction(2, 0, 2, 3)
            }
        }
        val machineA = getMachine().apply { changeTransitionFunction(0, 1, 1, 3) }
        val machineB = getMachine().apply { changeTransitionFunction(0, 0, 1, 4) }

        assertEquals(getMachine(), getMachine())
        assertEquals(machineA, machineA)
        assertEquals(machineB, machineB)
        assertNotEquals(machineA, getMachine())
        assertNotEquals(machineA, machineB)
    }

    @Test
    fun given_outputNotInOutputSet_when_changeTransitionFunctionCalled_then_throwIAException() {
        val machine = StateMachine(setOf(1), setOf(1), 1, 1)
        assertThrows(IllegalArgumentException::class.java) {
            machine.changeTransitionFunction(0, 0, 1, 0)
        }
    }

    @Test
    fun testRequiredPropertiesOfEquals() {
        (1..20).forEach {
            val machineA = StateMachine((1..3).toSet(), ('a'..'c').toSet(), 'a', 2).let { machine ->
                (1..3).zip('a'..'c').forEach {
                    val (input, output) = it
                    machine.changeTransitionFunction(0, 1, input, output)
                    machine.changeTransitionFunction(1, 0, input, output)
                }
            }
            val machineB = StateMachine((1..3).toSet(), ('a'..'c').toSet(), 'a', 2).let { machine ->
                (1..3).zip('a'..'c').forEach {
                    val (input, output) = it
                    machine.changeTransitionFunction(0, 1, input, output)
                    machine.changeTransitionFunction(1, 0, input, output)
                }
            }
            val machineC = StateMachine((1..3).toSet(), ('a'..'c').toSet(), 'a', 2).let { machine ->
                (1..3).zip('a'..'c').forEach {
                    val (input, output) = it
                    machine.changeTransitionFunction(0, 1, input, output)
                    machine.changeTransitionFunction(1, 0, input, output)
                }
            }
            assertTrue(machineA == machineA)    // Reflexivity
            assertTrue(machineA == machineB && machineB == machineA) // Symmetry
            assertTrue(machineA == machineB && machineB == machineC && machineA == machineC) // Transitivity
            assertFalse(machineA == null)   // Not equal to null
        }
    }

    @Test
    fun equalsReturnsFalseWhenComparingUnequalMachines() {
        unequalMachines.forEachIndexed { i, machineA ->
            unequalMachines.drop(i + 1).forEach { machineB ->
                assertNotEquals(machineA, machineB)
            }
        }
    }

    @Test
    fun given_copyOfStateMachine_when_callingDeepCopyConstructor_then_copyIsEqualToOriginal() {
        val getMachine = {
            StateMachine(setOf(1, 3, 4), setOf('a', 'c', 'd'), 'c', 3).apply {
                changeTransitionFunction(0, 0, 4, 'c')
                changeTransitionFunction(0, 2, 1, 'a')
            }
        }
        val toCopy = getMachine()
        assertEquals(toCopy, StateMachine(toCopy, toCopy.inputSet, toCopy.outputSet))
    }

    @Test
    fun given_freshObject_then_allStatesReachable() {
        val machine = StateMachine(setOf(0, 1, 2), setOf(3, 4), 3, 3)
        val visited = MutableList(3) { false }
        dfs(0, machine, visited)
        assertTrue(visited.reduce(Boolean::and))
    }

    private fun dfs(current: Int, machine: StateMachine<*, *>, visited: MutableList<Boolean>) {
        if (visited[current]) {
            return
        }
        visited[current] = true
        machine.getAdjacent(current).forEach {
            dfs(it, machine, visited)
        }
    }

    @Test
    fun when_callingGetAdjacent_then_returnsAdjacentStates() {
        val machine = StateMachine(setOf(1, 2), setOf(1), 1, 3).apply {
            val output = 1
            changeTransitionFunction(0, 0, 1, output)
            changeTransitionFunction(0, 0, 2, output)
            changeTransitionFunction(1, 0, 1, output)
            changeTransitionFunction(1, 2, 2, output)
            changeTransitionFunction(2, 1, 1, output)
            changeTransitionFunction(2, 1, 2, output)
        }
        val expectedNeighborhoods = listOf(setOf(0), setOf(0, 2), setOf(1))
        expectedNeighborhoods.forEachIndexed { i, expectedNeighborhood ->
            assertEquals(expectedNeighborhood, machine.getAdjacent(i))
        }
    }

    @Test
    fun testHashCode() {
        val machineA = makeMachine(defaultInput, defaultOutput, defaultStates)
        val machineB = makeMachine(defaultInput, defaultOutput, defaultStates)
        assertEquals(machineA.hashCode(), machineB.hashCode())
    }

    @Test
    fun given_firstTimeCalling_when_nextCalled_then_callAndReturnGetFirstOutput() {
        val initOutput = 2
        val machine = spyk(StateMachine(setOf(0), setOf(1, 2), initOutput, 1).apply {
            changeTransitionFunction(0, 0, 0, 1)
        })
        assertEquals(initOutput, machine.next(0))
        verify { machine.getFirstOutput() }
    }

    @Test
    fun given_stateMachine_then_nextReturnsExpectedSequence() {
        val machine = StateMachine(setOf(1, 2), setOf('a', 'b'), 'b', 2).apply {
            changeTransitionFunction(0, 0, 1, 'b')
            changeTransitionFunction(0, 1, 2, 'a')
            changeTransitionFunction(1, 1, 2, 'a')
            changeTransitionFunction(1, 0, 1, 'a')
        }
        val inputs = listOf(2, 1, 2, 2, 1, 1, 1, 2, 1)
        val expectedOutputs = listOf('b', 'b', 'a', 'a', 'a', 'b', 'b', 'a', 'a')
        inputs.zip(expectedOutputs).forEach { (input, expectedOutput) ->
            assertEquals(expectedOutput, machine.next(input))
        }
    }

    @Test
    fun given_copy_then_originalAndCopyIndependent() {
        data class Dummy(var v: Int)
        val inputDummy = Dummy(1)
        val outputDummy = Dummy(2)
        val runCase = { modifyOriginal: (StateMachine<Dummy, Dummy>) -> Unit ->
            val original = StateMachine(setOf(inputDummy), setOf(outputDummy), outputDummy, 2).apply {
                changeTransitionFunction(0, 0, inputDummy, outputDummy)
                changeTransitionFunction(1, 1, inputDummy, outputDummy)
            }
            val copy = StateMachine(original, setOf(inputDummy.copy()), setOf(outputDummy.copy()))
            modifyOriginal(original)
            assertNotEquals(original, copy)
        }

        runCase { inputDummy.v += 1 }
        runCase { outputDummy.v += 1 }
        runCase { it.changeTransitionFunction(0, 1, inputDummy, outputDummy) }
    }
}