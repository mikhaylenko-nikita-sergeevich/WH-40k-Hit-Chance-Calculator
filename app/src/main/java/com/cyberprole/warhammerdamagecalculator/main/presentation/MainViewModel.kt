package com.cyberprole.warhammerdamagecalculator.main.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberprole.warhammerdamagecalculator.R
import com.cyberprole.warhammerdamagecalculator.main.usecases.DamageCalculatorUseCase
import com.cyberprole.warhammerdamagecalculator.main.usecases.InputData
import com.cyberprole.warhammerdamagecalculator.main.usecases.OutputData
import com.cyberprole.warhammerdamagecalculator.providers.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

sealed class UiState : Serializable {
    object INITIAL : UiState()
    object CALCULATING : UiState()
    data class CALCULATED(val result: String) : UiState()
    data class ERROR(val error: String) : UiState()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val damageCalculatorUseCase: DamageCalculatorUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialState = savedStateHandle.get<UiState>("uiState") ?: UiState.INITIAL
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private fun updateUiState(state: UiState) {
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
        updateUiState(UiState.CALCULATING)

        viewModelScope.launch {
            val tasks = listOf(
                async(Dispatchers.IO) {
                    val wsbsValue = when {
                        wsbs == stringProvider.getStrings(R.array.wsbs_values)[0] -> null
                        else -> Integer.parseInt(wsbs[0].toString())
                    }

                    val invulnerableSaveValue = when {
                        invulnerableSave == stringProvider.getStrings(R.array.inv_save_values)[0] -> null
                        else -> Integer.parseInt(invulnerableSave[0].toString())
                    }

                    val feelNoPainValue = when {
                        feelNoPain == stringProvider.getStrings(R.array.fnp_save_values)[0] -> null
                        else -> Integer.parseInt(feelNoPain[0].toString())
                    }

                    val inputData = InputData(
                        wsbsValue,
                        Integer.parseInt(strength),
                        Integer.parseInt(toughness),
                        Integer.parseInt(ap),
                        Integer.parseInt(save[0].toString()),
                        invulnerableSaveValue,
                        feelNoPainValue,
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

                    val data = damageCalculatorUseCase.calculateDamage(inputData)

                    formatResult(data)
                },
                async {
                    delay(1000) //ждём одну секунду или до тех пор пока не посчитается
                }
            )
            val results = tasks.awaitAll()

            updateUiState(UiState.CALCULATED(results[0] as String))
        }
    }

    private fun formatResult(outputData: OutputData): String =
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


    private fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
}