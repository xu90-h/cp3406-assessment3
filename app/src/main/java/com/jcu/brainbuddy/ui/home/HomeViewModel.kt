package com.jcu.brainbuddy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcu.brainbuddy.data.repository.ResultRepository
import com.jcu.brainbuddy.data.repository.SettingsRepository
import com.jcu.brainbuddy.domain.AppSettings
import com.jcu.brainbuddy.domain.StatsCalculator
import com.jcu.brainbuddy.domain.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val settings: AppSettings = AppSettings(),
    val stats: UserStats = UserStats()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    resultRepository: ResultRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        settingsRepository.settings,
        resultRepository.observeResults()
    ) { settings, results ->
        HomeUiState(settings, StatsCalculator.compute(results))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
