package com.droidcon.weatherscope.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.droidcon.weatherscope.ui.common.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState<SettingsState>>(ScreenState.Loading)
    val screenState: StateFlow<ScreenState<SettingsState>> = _screenState

    init {

        _screenState.value = ScreenState.Success(SettingsState("settings viewmodel data."))
    }
}