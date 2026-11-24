package com.jcu.brainbuddy.ui

import androidx.lifecycle.SavedStateHandle
import com.jcu.brainbuddy.FakeQuestionRepository
import com.jcu.brainbuddy.FakeResultRepository
import com.jcu.brainbuddy.FakeSettingsRepository
import com.jcu.brainbuddy.data.repository.QuestionBatch
import com.jcu.brainbuddy.domain.AppSettings
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.question
import com.jcu.brainbuddy.ui.quiz.QuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private lateinit var questions: FakeQuestionRepository
    private lateinit var results: FakeResultRepository
    private lateinit var settings: FakeSettingsRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        questions = FakeQuestionRepository()
        results = FakeResultRepository()
        settings = FakeSettingsRepository(
            AppSettings(category = QuizCategory.MATH, difficulty = Difficulty.MEDIUM, questionCount = 10)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(category: String? = null) = QuizViewModel(
        SavedStateHandle(if (category == null) emptyMap() else mapOf(QuizViewModel.CATEGORY_ARG to category)),
        questions,
        results,
        settings
    )

    @Test
    fun `loads questions using the saved settings`() {
        val vm = viewModel()
        val state = vm.uiState.value

        assertFalse(state.isLoading)
        assertNotNull(state.progress)
        assertEquals(QuizCategory.MATH, questions.lastCategory)
        assertEquals(Difficulty.MEDIUM, questions.lastDifficulty)
        assertEquals(10, questions.lastCount)
    }

    @Test
    fun `category argument overrides the saved topic`() {
        val vm = viewModel("SCIENCE")

        assertEquals(QuizCategory.SCIENCE, questions.lastCategory)
        assertEquals(QuizCategory.SCIENCE, vm.uiState.value.category)
    }

    @Test
    fun `offline fallback is surfaced to the screen`() {
        questions.batch = QuestionBatch(listOf(question()), isOnline = false)

        assertFalse(viewModel().uiState.value.isOnline)
    }

    @Test
    fun `answering updates progress and ignores a second tap`() {
        val vm = viewModel()

        vm.onAnswerSelected(0)
        vm.onAnswerSelected(2)

        val progress = vm.uiState.value.progress!!
        assertEquals(0, progress.selectedIndex)
        assertEquals(1, progress.correctCount)
    }

    @Test
    fun `next does nothing before an answer is chosen`() {
        val vm = viewModel()

        vm.onNext()

        assertEquals(0, vm.uiState.value.progress!!.currentIndex)
    }

    @Test
    fun `finishing the quiz saves exactly one result`() = runTest {
        val vm = viewModel("ANIMALS")

        vm.onAnswerSelected(0)
        vm.onNext()
        vm.onAnswerSelected(1)
        vm.onNext()
        vm.onNext()

        val saved = results.observeResults().first()
        assertEquals(1, saved.size)
        assertEquals(QuizCategory.ANIMALS, saved.single().category)
        assertEquals(2, saved.single().totalQuestions)
        assertEquals(1, saved.single().correctAnswers)
        assertEquals(saved.single(), vm.uiState.value.completedResult)
    }

    @Test
    fun `a quiz that is not finished is never saved`() = runTest {
        val vm = viewModel()

        vm.onAnswerSelected(0)
        vm.onNext()

        assertTrue(results.observeResults().first().isEmpty())
        assertNull(vm.uiState.value.completedResult)
    }
}
