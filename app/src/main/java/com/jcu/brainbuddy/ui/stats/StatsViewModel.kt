package com.jcu.brainbuddy.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcu.brainbuddy.data.repository.ResultRepository
import com.jcu.brainbuddy.domain.StatsCalculator
import com.jcu.brainbuddy.domain.UserStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    resultRepository: ResultRepository
) : ViewModel() {

    val stats: StateFlow<UserStats> = resultRepository.observeResults()
        .map { StatsCalculator.compute(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserStats())
}
