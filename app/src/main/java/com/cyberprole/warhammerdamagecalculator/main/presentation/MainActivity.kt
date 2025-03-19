package com.cyberprole.warhammerdamagecalculator.main.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cyberprole.warhammerdamagecalculator.R
import com.cyberprole.warhammerdamagecalculator.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private val TAG = MainActivity::class.simpleName

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSpinners()
        initEditTexts()

        binding.calculateButton.setOnClickListener {
            if (isTextFieldsValid()) viewModel.onCalculateClicked(
                wsbs = binding.attackerLayout.wsbsSpinner.text.toString(),
                strength = binding.attackerLayout.strengthEdittext.text.toString(),
                toughness = binding.defenderLayout.toughnessEdittext.text.toString(),
                ap = binding.attackerLayout.apSpinner.text.toString(),
                save = binding.defenderLayout.saveSpinner.text.toString(),
                invulnerableSave = binding.defenderLayout.invulnerableSaveSpinner.text.toString(),
                feelNoPain = binding.defenderLayout.feelNoPainSpinner.text.toString(),
                isLethalHits = binding.attackerLayout.lethalHitsCheckbox.isChecked,
                isDevastatingWounds = binding.attackerLayout.devastatingWoundsCheckbox.isChecked,
                isToWoundImprove = binding.attackerLayout.towoundImprove.isChecked,
                isToWoundDecrease = binding.defenderLayout.towoundDecrease.isChecked,
                isCover = binding.defenderLayout.cover.isChecked,
                isToHitReRollOf1 = binding.attackerLayout.tohitRerollOf1Checkbox.isChecked,
                isToHitReRollFull = binding.attackerLayout.tohitRerollFullCheckbox.isChecked,
                isToWoundReRollOf1 = binding.attackerLayout.towoundRerollOf1Checkbox.isChecked,
                isToWoundReRollFull = binding.attackerLayout.towoundRerollFullCheckbox.isChecked,
                isFnpAgainstMortalWoundsOnly = binding.defenderLayout.fnpAgainstMortalWoundsOnlyCheckbox.isChecked
            )
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> onUiStateChanged(state) }
            }
        }
    }

    private fun initEditTexts() {
        //очищаем сообщения об ошибке при изменении текста в поле ввода
        //валидность проверяется при нажатии кнопки
        binding.attackerLayout.strengthEdittext.addTextChangedListener {
            binding.attackerLayout.strengthTextField.error = ""
        }
        binding.defenderLayout.toughnessEdittext.addTextChangedListener {
            binding.defenderLayout.toughnessTextField.error = ""
        }
    }

    private fun initSpinners() {
        val listWSBS = resources.getStringArray(R.array.wsbs_values)
        val listAP = resources.getStringArray(R.array.ap_values)
        val listSave = resources.getStringArray(R.array.save_values)
        val listInvulnerableSave = resources.getStringArray(R.array.invulnerable_save_values)
        val listFNP = resources.getStringArray(R.array.fnp_save_values)

        binding.attackerLayout.wsbsSpinner.setText(listWSBS[2], false)
        binding.attackerLayout.apSpinner.setText(listAP[0], false)
        binding.defenderLayout.saveSpinner.setText(listSave[1], false)
        binding.defenderLayout.invulnerableSaveSpinner.setText(listInvulnerableSave[0], false)
        binding.defenderLayout.feelNoPainSpinner.setText(listFNP[0], false)
    }

    private fun isTextFieldsValid(): Boolean {
        val isStrengthEmpty = binding.attackerLayout.strengthEdittext.text?.isEmpty() ?: true
        val isToughnessEmpty = binding.defenderLayout.toughnessEdittext.text?.isEmpty() ?: true

        if (isStrengthEmpty) binding.attackerLayout.strengthTextField.error = "field can't be empty"
        if (isToughnessEmpty) binding.defenderLayout.toughnessTextField.error =
            "field can't be empty"

        return !isStrengthEmpty && !isToughnessEmpty
    }

    private fun onUiStateChanged(state: State) {
        when (state) {
            is State.INITIAL -> {
                binding.resultLabelTextview.visibility = View.GONE
                binding.resultTextview.visibility = View.GONE
                binding.resultTextview.text = ""
            }

            is State.CALCULATING -> {
                //todo
            }

            is State.CALCULATED -> {
                binding.resultLabelTextview.visibility = View.VISIBLE
                binding.resultTextview.visibility = View.VISIBLE
                binding.resultTextview.text = state.result
            }

            is State.ERROR -> {
                //todo
            }
        }
    }
}