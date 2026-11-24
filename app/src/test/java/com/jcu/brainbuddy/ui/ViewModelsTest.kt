package com.jcu.brainbuddy.ui

import com.jcu.brainbuddy.FakeResultRepository
import com.jcu.brainbuddy.FakeSettingsRepository
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.result
import com.jcu.brainbuddy.ui.home.HomeViewModel
import com.jcu.brainbuddy.ui.settings.SettingsViewModel
import com.jcu.brainbuddy.ui.stats.StatsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {

    private lateinit var settings: FakeSettingsRepository
    private lateinit var results: FakeResultRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        settings = FakeSettingsRepository()
        results = FakeResultRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.collect(flow: Flow<*>) =
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { flow.collect { } }

    @Test
    fun `settings changes are written through to the repository`() = runTest {
        val vm = SettingsViewModel(settings, results)
        val job = collect(vm.settings)

        vm.setCategory(QuizCategory.ANIMALS)
        vm.setDifficulty(Difficulty.HARD)
        vm.setQuestionCount(10)
        vm.setLargeText(true)
        vm.setHaptics(false)

        val current = vm.settings.value
        assertEquals(QuizCategory.ANIMALS, current.category)
        assertEquals(Difficulty.HARD, current.difficulty)
        assertEquals(10, current.questionCount)
        assertTrue(current.largeText)
        assertFalse(current.hapticsEnabled)
        job.cancel()
    }

    @Test
    fun `clearing progress deletes every saved result`() = runTest {
        results.save(result())
        results.save(result())
        val vm = SettingsViewModel(settings, results)

        vm.clearProgress()

        val stats = StatsViewModel(results)
        val job = collect(stats.stats)
        assertFalse(stats.stats.value.hasData)
        job.cancel()
    }

    @Test
    fun `stats view model reflects saved results`() = runTest {
        results.save(result(correct = 5, total = 5))
        val vm = StatsViewModel(results)
        val job = collect(vm.stats)

        assertEquals(1, vm.stats.value.totalQuizzes)
        assertEquals(100, vm.stats.value.accuracyPercent)

        results.save(result(correct = 0, total = 5))
        assertEquals(2, vm.stats.value.totalQuizzes)
        assertEquals(50, vm.stats.value.accuracyPercent)
        job.cancel()
    }

    @Test
    fun `home view model combines settings and stats`() = runTest {
        settings.setCategory(QuizCategory.GEOGRAPHY)
        results.save(result(correct = 3, total = 5))
        val vm = HomeViewModel(settings, results)
        val job = collect(vm.uiState)

        assertEquals(QuizCategory.GEOGRAPHY, vm.uiState.value.settings.category)
        assertEquals(1, vm.uiState.value.stats.totalQuizzes)
        job.cancel()
    }
}
