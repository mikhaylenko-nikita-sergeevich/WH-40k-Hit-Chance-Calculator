package com.cyberprole.warhammerdamagecalculator.main.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cyberprole.warhammerdamagecalculator.main.DamageCalculatorUseCase
import com.cyberprole.warhammerdamagecalculator.main.InputData
import com.cyberprole.warhammerdamagecalculator.round
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
    private val damageCalculatorUseCase: DamageCalculatorUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialState = savedStateHandle.get<State>("uiState") ?: State.INITIAL
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private fun updateUiState(state: State) {
        savedStateHandle["uiState"] = state
        _uiState.value = state
    }

    fun onCalculateClicked(
        wsbs: String,
        strength: String,
        toughness: String,
        ap: String,
        save: String,
        invulnerableSave: String,
        feelNoPain: String,
        isLethalHits: Boolean,
        isDevastatingWounds: Boolean,
        isToWoundImprove: Boolean,
        isToWoundDecrease: Boolean,
        isCover: Boolean,
        isToHitReRollOf1: Boolean,
        isToHitReRollFull: Boolean,
        isToWoundReRollOf1: Boolean,
        isToWoundReRollFull: Boolean,
        isFnpAgainstMortalWoundsOnly: Boolean
    ) {
        val inputData = InputData(
            if (wsbs != "N/A") Integer.parseInt(wsbs[0].toString()) else null,
            Integer.parseInt(strength),
            Integer.parseInt(toughness),
            Integer.parseInt(ap),
            Integer.parseInt(save[0].toString()),
            if (invulnerableSave != "-") Integer.parseInt(invulnerableSave[0].toString()) else null,
            if (feelNoPain != "-") Integer.parseInt(feelNoPain[0].toString()) else null,
            isLethalHits,
            isDevastatingWounds,
            isToWoundImprove,
            isToWoundDecrease,
            isCover,
            isToHitReRollOf1,
            isToHitReRollFull,
            isToWoundReRollOf1,
            isToWoundReRollFull,
            isFnpAgainstMortalWoundsOnly
        )

        updateUiState(State.CALCULATING)

        val result = damageCalculatorUseCase.calculateDamage(inputData).let { outputData ->
            "Hit chance: ${(outputData.hitChance * 100).round(1)}%\n" +
                    "Wound chance: ${(outputData.woundChance * 100).round(1)}%\n" +
                    "Save chance: ${(outputData.saveChance * 100).round(1)}%\n" +
                    "FNP chance: ${(outputData.fnpChance * 100).round(1)}\n\n" +
                    "Chance of inflicting like normal damage: ${
                        (outputData.chanceOfInflictingNormalDamage * 100).round(
                            1
                        )
                    }%\n" +
                    "Chance of inflicting damage like mortal wounds: ${
                        (outputData.chanceOfInflictingDamageLikeMortalWounds * 100).round(
                            1
                        )
                    }%"
        }
        updateUiState(State.CALCULATED(result))
    }
}