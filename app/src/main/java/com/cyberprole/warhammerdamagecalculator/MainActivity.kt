package com.cyberprole.warhammerdamagecalculator

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cyberprole.warhammerdamagecalculator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

private val TAG = MainActivity::class.simpleName

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var listWSBS: Array<String>
    private lateinit var listAP: Array<String>
    private lateinit var listSave: Array<String>
    private lateinit var listInvulnerableSave: Array<String>
    private lateinit var listFNP: Array<String>

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listWSBS = resources.getStringArray(R.array.wsbs_values)
        listAP = resources.getStringArray(R.array.ap_values)
        listSave = resources.getStringArray(R.array.save_values)
        listInvulnerableSave = resources.getStringArray(R.array.invulnerable_save_values)
        listFNP = resources.getStringArray(R.array.fnp_save_values)

        initSpinners()

        binding.attackerLayout.strengthEdittext.addTextChangedListener {
            binding.attackerLayout.strengthTextField.error = ""
        }
        binding.defenderLayout.toughnessEdittext.addTextChangedListener {
            binding.defenderLayout.toughnessTextField.error = ""
        }

        binding.calculateButton.setOnClickListener {
            if (isTextFieldsValid()) viewModel.onCalculateClicked(
                wsbs = binding.attackerLayout.wsbsSpinner.text.toString(),
                strength = binding.attackerLayout.strengthEdittext.text.toString(),
                toughness = binding.defenderLayout.toughnessEdittext.text.toString(),
                ap = binding.attackerLayout.apSpinner.text.toString(),
                save = binding.defenderLayout.saveSpinner.text.toString(),
                invulnerableSave = binding.defenderLayout.invulnerableSaveSpinner.text.toString(),
                feelNoPain = binding.defenderLayout.feelNoPainSpinner.text.toString(),
                lethalHits = binding.attackerLayout.lethalHitsCheckbox.isChecked,
                devastatingWounds = binding.attackerLayout.devastatingWoundsCheckbox.isChecked,
                towoundImprove = binding.attackerLayout.towoundImprove.isChecked,
                towoundDecrease = binding.defenderLayout.decreaseTowound.isChecked,//todo rename
                isCover = binding.defenderLayout.cover.isChecked,
                isToHitRerollOf1 = binding.attackerLayout.tohitRerollOf1Checkbox.isChecked,
                isToHitRerollFull = binding.attackerLayout.tohitRerollFullCheckbox.isChecked,
                isToWoundRerollOf1 = binding.attackerLayout.towoundRerollOf1Checkbox.isChecked,
                isToWoundRerollFull = binding.attackerLayout.towoundRerollFullCheckbox.isChecked,
                isFnpAgainstMortalWoundsOnly = binding.defenderLayout.fnpAgainstMortalWoundsOnlyCheckbox.isChecked
            )
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
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
        }
    }

    private fun initSpinners() {
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
}