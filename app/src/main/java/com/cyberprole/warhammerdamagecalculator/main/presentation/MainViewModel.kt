package com.cyberprole.warhammerdamagecalculator.main.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cyberprole.warhammerdamagecalculator.main.CalcalationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.Serializable
import javax.inject.Inject

sealed class State : Serializable {
    object INITIAL : State()
    object CALCULATING : State()
    class CALCULATED(val result: String) : State()
    object ERROR : State()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    val calcalationUseCase: CalcalationUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialState = savedStateHandle.get<State>("uiState") ?: State.INITIAL
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    fun onCalculateClicked(
        wsbs: String,
        strength: String,
        toughness: String,
        ap: String,
        save: String,
        invulnerableSave: String,
        feelNoPain: String,
        lethalHits: Boolean,
        devastatingWounds: Boolean,
        towoundImprove: Boolean,
        towoundDecrease: Boolean,
        isCover: Boolean,
        isToHitRerollOf1: Boolean,
        isToHitRerollFull: Boolean,
        isToWoundRerollOf1: Boolean,
        isToWoundRerollFull: Boolean,
        isFnpAgainstMortalWoundsOnly: Boolean
    ) {
        updateUiState(State.CALCULATING)

        val result = calcalationUseCase.calculateDamage(
            wsbs,
            strength,
            toughness,
            ap,
            save,
            invulnerableSave,
            feelNoPain,
            lethalHits,
            devastatingWounds,
            towoundImprove,
            towoundDecrease,
            isCover,
            isToHitRerollOf1,
            isToHitRerollFull,
            isToWoundRerollOf1,
            isToWoundRerollFull,
            isFnpAgainstMortalWoundsOnly
        )

        updateUiState(State.CALCULATED(result))
    }

    private fun updateUiState(state: State) {
        savedStateHandle["uiState"] = state
        _uiState.value = state
    }
}