import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StateMachineTest {

    @Test
    fun given_emptyInputAlphabet_then_throwIAException() {}

    @Test
    fun given_emptyOutputAlphabet_then_throwIAException() {}

    @Test
    fun given_initialOutputNotInOutputAlphabet_then_throwIAException() {}

    @Test
    fun given_getFirstOutput_then_returnInitialOutput() {}

    @Test
    fun given_getFirstOutput_when_alreadyCalledOnce_then_throwRunTimeException() {}

    @Test
    fun stateMachineIntegrationTests() {}

    @Test
    fun given_changeTransitionFunctionEntry_when_fromStateOrToStateOutsideBounds_throwIAException() {}

    @Test
    fun given_nonPositiveNumberOfStates_then_throwIAException() {}

    @Test
    fun given_inputNotInInputAlphabet_when_getOutputCalled_then_throwIAException() {}

    @Test
    fun given_inputNotInInputAlphabet_when_changeTransitionFunctionEntryCalled_then_throwIAException() {}

    @Test
    fun given_changeTransitionFunctionEntry_then_transitionFunctionUpdatesCorrectly() {}

    @Test
    fun given_outputNotInOutputAlphabet_when_changeTransitionFunctionEntryCalled_then_throwIAException() {}

    @Test
    fun testEquals() {
        TODO("Test reflexivity")
        TODO("Test symmetry")
        TODO("Test transitivity")
        TODO("Test never equal to null")
        TODO("Test consistency")
    }

    @Test
    fun given_copyOfStateMachine_then_copyIsEqualToOriginal() {}

}