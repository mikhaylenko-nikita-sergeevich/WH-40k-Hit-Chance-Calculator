package com.cyberprole.warhammerdamagecalculator

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.cyberprole.warhammerdamagecalculator.databinding.Activity10thMainBinding

private val TAG = Main10thActivity::class.simpleName

class Main10thActivity : AppCompatActivity() {

    private lateinit var binding: Activity10thMainBinding
    private lateinit var listWSBS: Array<String>
    private lateinit var listAP: Array<String>
    private lateinit var listSave: Array<String>
    private lateinit var listInvulnerableSave: Array<String>
    private lateinit var listFNP: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity10thMainBinding.inflate(layoutInflater)
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
            if (isTextFieldsValid()) calculateDamage()
        }
    }

    private fun isTextFieldsValid(): Boolean {
        val isStrengthEmpty = binding.attackerLayout.strengthEdittext.text?.isEmpty() ?: true
        val isToughnessEmpty = binding.defenderLayout.toughnessEdittext.text?.isEmpty() ?: true

        if (isStrengthEmpty) binding.attackerLayout.strengthTextField.error = "field can't be empty"
        if (isToughnessEmpty) binding.defenderLayout.toughnessTextField.error =
            "field can't be empty"

        return !isStrengthEmpty && !isToughnessEmpty
    }

    private fun initSpinners() {
        binding.attackerLayout.wsbsSpinner.setText(listWSBS[0], false)
        binding.attackerLayout.apSpinner.setText(listAP[0], false)
        binding.defenderLayout.saveSpinner.setText(listSave[0], false)
        binding.defenderLayout.invulnerableSaveSpinner.setText(listInvulnerableSave[0], false)
        binding.defenderLayout.feelNoPainSpinner.setText(listFNP[0], false)
    }

    private fun hitNonCriticalChance(): Double {
        val selected = binding.attackerLayout.wsbsSpinner.text.toString()
        if (selected == "N/A")
            return 1.0
        else {
            val firstSymbol = selected[0]
            val param = Integer.parseInt(firstSymbol.toString())

            val successRollChance = d6(param)

            return successRollChance - hitCriticalChance()
        }
    }

    private fun hitCriticalChance(): Double {
        return if (binding.attackerLayout.wsbsSpinner.text.toString() == "N/A") 0.0
        else 1.0 / 6
    }

    private fun woundNonCriticalChance(): Double {
        val ds: Double =
            Integer.parseInt(binding.attackerLayout.strengthEdittext.text.toString()).toDouble()
        val dt: Double =
            Integer.parseInt(binding.defenderLayout.toughnessEdittext.text.toString()).toDouble()

        var param = when {
            ds <= dt / 2 -> 6
            ds < dt -> 5
            ds == dt -> 4
            ds < dt * 2 -> 3
            ds >= dt * 2 -> 2
            else -> throw Exception("PARAM INVALID")
        }

        //add +1 to wound roll
        if (binding.attackerLayout.towoundImprove.isChecked && param in 3..6) param -= 1
        if (binding.defenderLayout.decreaseTowound.isChecked && param in 2..5) param += 1

        val successRollChance = d6(param)
        return successRollChance - woundCriticalChance()
    }

    private fun woundCriticalChance(): Double {
        return 1.0 / 6
    }

    private fun saveFailedChance(): Double {
        //AP — отрицательная величина, на неё ухудшается базовый сейв
        val ap = Integer.parseInt(binding.attackerLayout.apSpinner.text.toString())
        val save =
            Integer.parseInt(binding.defenderLayout.saveSpinner.text.toString()[0].toString())
        val invString =
            binding.defenderLayout.invulnerableSaveSpinner.text.toString()[0].toString()

        val inv = when (invString) {
            "-" -> 7
            else -> Integer.parseInt(invString)
        }

        var modifiedSave = save
        if (binding.defenderLayout.cover.isChecked && (save > 3 || ap < 0)) modifiedSave =
            modifiedSave - ap - 1
        else modifiedSave = modifiedSave - ap

        //выбираем лучший показатель между обычным сейвом и инвулём
        val saveChance = when {
            modifiedSave <= inv -> d6(modifiedSave)
            else -> d6(inv)
        }

        return 1.0 - saveChance
    }

    private fun fnp(): Double {
        val string = binding.defenderLayout.feelNoPainSpinner.text.toString()[0].toString()

        if (string != "-") {
            val param = Integer.parseInt(string)
            return d6(param)
        } else return 0.0
    }

    private fun calculateDamage() {
        val lh = binding.attackerLayout.lethalHitsCheckbox.isChecked
        val dw = binding.attackerLayout.devastatingWoundsCheckbox.isChecked

        var hnc = hitNonCriticalChance()
        var hc = hitCriticalChance()
        var wnc = woundNonCriticalChance()
        var wc = woundCriticalChance()
        val save = saveFailedChance()

        var hr = 1.0
        var wr = 1.0

        //re-rolls, full re-roll is better, that re-rolls of "1"
        if (binding.attackerLayout.wsbsSpinner.text.toString() != listWSBS[0]) {
            if (binding.attackerLayout.tohitRerollOf1Checkbox.isChecked) hr = 1 + 1.0 / 6
            if (binding.attackerLayout.tohitRerollFullCheckbox.isChecked) hr = 1 + (1 - (hnc + hc))
        }

        if (binding.attackerLayout.towoundRerollOf1Checkbox.isChecked) wr = 1 + 1.0 / 6
        if (binding.attackerLayout.towoundRerollFullCheckbox.isChecked) wr = 1 + (1 - (wnc + wc))

        //apply re-rolls multipliers
        hnc *= hr
        hc *= hr

        wnc *= wr
        wc *= wr

        //<basic damage, mortal wounds>
        var result: Pair<Double, Double> = when {

            !lh && dw -> Pair(
                hnc * wnc * save + hc * wnc * save,
                hnc * wc * 1 + hc * wc * 1
            )

            lh && !dw -> Pair(
                hnc * wnc * save + hnc * wc * save + hc * 1 * save + hc * 1 * save,
                0.0
            )

            lh && dw -> Pair(
                hnc * wnc * save + hc * 1 * save + hc * 1 * save,
                hnc * wc * 1
            )

            else -> Pair(//базовый вариант без модификаторов
                (hnc * wnc * save) + (hnc * wc * save) + (hc * wnc * save) + (hc * wc * save),
                0.0
            )
        }

        val fnp = fnp()
        result = Pair(result.first, result.second * (1.0 - fnp))
        if (!binding.defenderLayout.fnpAgainstMortalWoundsOnlyCheckbox.isChecked) {
            result = Pair(result.first * (1.0 - fnp), result.second)
        }

        binding.resultLabelTextview.visibility = View.VISIBLE
        binding.resultTextview.visibility = View.VISIBLE
        binding.resultTextview.text = "Successfull hit chance: ${((hnc + hc) * 100).round(1)}%\n" +
                "Successfull wound chance: ${((wnc + wc) * 100).round(1)}%\n" +
                "Successfull save chance: ${((1 - save) * 100).round(1)}%\n" +
                "Successfull FNP chance: ${(fnp * 100).round(1)}\n\n" +
                "Chance of inflicting normal damage: ${(result.first * 100).round(1)}%\n" +
                "Chance of inflicting damage like mortal wounds: ${(result.second * 100).round(1)}%"
    }

    //tools
    private fun d6(param: Int): Double = when (param) {
        in 2..6 -> (6 - (param - 1)).toDouble() / 6
        else -> 0.0
    }

    private fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

}