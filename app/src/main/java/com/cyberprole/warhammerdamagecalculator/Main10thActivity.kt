package com.cyberprole.warhammerdamagecalculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.cyberprole.warhammerdamagecalculator.databinding.Activity10thMainBinding

private val TAG = Main10thActivity::class.simpleName

class Main10thActivity : AppCompatActivity() {

    private lateinit var binding: Activity10thMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Activity10thMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSpinners()

        binding.calculateButton.setOnClickListener {
            calculateDamage()
        }
    }

    private fun initSpinners() {
        ArrayAdapter.createFromResource(
            this,
            R.array.wsbs_values,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.wsbsSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.ap_values,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.apSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.damage_values,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.damageSpinner.isEnabled = false
            binding.damageSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.save_values,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.saveSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.invulnerable_save_values,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.invulnerableSaveSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.fnp_save_values,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.feelNoPainSpinner.adapter = adapter
        }

        binding.wsbsSpinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "Selected WS/BS: ${binding.wsbsSpinner.adapter.getItem(position)}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "SW/BS was not selected!")
            }
        })

        binding.apSpinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "Selected AP: ${binding.apSpinner.adapter.getItem(position)}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "AP was not selected!")
            }
        })

        binding.damageSpinner.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d(
                        TAG,
                        "Selected Damage: ${binding.damageSpinner.adapter.getItem(position)}"
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d(TAG, "Damage was not selected!")
                }
            })

        binding.saveSpinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "Selected Save: ${binding.saveSpinner.adapter.getItem(position)}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "Save was not selected!")
            }
        })

        binding.invulnerableSaveSpinner.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d(
                        TAG,
                        "Selected Invulnerable Save: ${
                            binding.invulnerableSaveSpinner.adapter.getItem(position)
                        }"
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d(TAG, "Invulnerable Save was not selected!")
                }
            })

        binding.feelNoPainSpinner.onItemSelectedListener =
            (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Log.d(
                        TAG,
                        "Selected FNP: ${binding.feelNoPainSpinner.adapter.getItem(position)}"
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d(TAG, "FNP was not selected!")
                }
            })
    }

    private fun hitNonCriticalChance(): Double {
        if (binding.wsbsSpinner.selectedItem == "N/A")
            return 1.0
        else {
            val firstSymbol = binding.wsbsSpinner.selectedItem.toString()[0]
            val param = Integer.parseInt(firstSymbol.toString())

            val successRollChance = d6(param)

            return successRollChance - hitCriticalChance()
        }
    }

    private fun hitCriticalChance(): Double {
        return if (binding.wsbsSpinner.selectedItem == "N/A") 0.0
        else 1.0 / 6
    }

    private fun woundNonCriticalChance(): Double {
        val ds: Double = Integer.parseInt(binding.strengthEdittext.text.toString()).toDouble()
        val dt: Double = Integer.parseInt(binding.toughnessEdittext.text.toString()).toDouble()

        var param = when {
            ds <= dt / 2 -> 6
            ds < dt -> 5
            ds == dt -> 4
            ds < dt * 2 -> 3
            ds >= dt * 2 -> 2
            else -> throw Exception("PARAM INVALID")
        }

        //add +1 to wound roll
        if (binding.towoundImprove.isChecked && param in 3..6) param -= 1
        if (binding.decreaseTowound.isChecked && param in 2..5) param += 1

        val successRollChance = d6(param)
        return successRollChance - woundCriticalChance()
    }

    private fun woundCriticalChance(): Double {
        return 1.0 / 6
    }

    private fun saveFailedChance(): Double {
        //AP — отрицательная величина, на неё ухудшается базовый сейв
        val ap = Integer.parseInt(binding.apSpinner.selectedItem.toString())
        val save = Integer.parseInt(binding.saveSpinner.selectedItem.toString()[0].toString())
        val invString = binding.invulnerableSaveSpinner.selectedItem.toString()[0].toString()

        val inv = when (invString) {
            "-" -> 7
            else -> Integer.parseInt(invString)
        }

        var modifiedSave = save
        if (binding.cover.isChecked && (save > 3 || ap < 0)) modifiedSave = modifiedSave - ap - 1
        else modifiedSave = modifiedSave - ap

        //выбираем лучший показатель между обычным сейвом и инвулём
        val saveChance = when {
            modifiedSave <= inv -> d6(modifiedSave)
            else -> d6(inv)
        }

        return 1.0 - saveChance
    }

    private fun fnp(): Double {
        val string = binding.feelNoPainSpinner.selectedItem.toString()[0].toString()

        if (string != "-") {
            val param = Integer.parseInt(string)
            return d6(param)
        } else return 0.0
    }

    private fun calculateDamage() {
        val lh = binding.lethalHitsCheckbox.isChecked
        val dw = binding.devastatingWoundsCheckbox.isChecked

        var hnc = hitNonCriticalChance()
        var hc = hitCriticalChance()
        var wnc = woundNonCriticalChance()
        var wc = woundCriticalChance()
        val save = saveFailedChance()

        var hr = 1.0
        var wr = 1.0

        //re-rolls, full re-roll is better, that re-rolls of "1"
        if (binding.tohitRerollOf1Checkbox.isChecked) hr = 1 + 1.0 / 6
        if (binding.tohitRerollFullCheckbox.isChecked) hr = 1 + (1 - (hnc + hc))

        if (binding.towoundRerollFullCheckbox.isChecked) wr = 1 + 1.0 / 6
        if (binding.towoundRerollOf1Checkbox.isChecked) wr = 1 + (1 - (wnc + wc))

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
        if (!binding.fnpAgainstMortalWoundsOnlyCheckbox.isChecked) {
            result = Pair(result.first * (1.0 - fnp), result.second)
        }

        binding.resultTextview.text = "Result:\n" +
                "hit chance: ${((hnc + hc) * 100).round(1)}%\n" +
                "wound chance: ${((wnc + wc) * 100).round(1)}%\n" +
                "save chance: ${((1 - save) * 100).round(1)}%\n" +
                "fnp chance: ${(fnp * 100).round(1)}\n" +
                "chance that attack will inflict normal damage: ${(result.first * 100).round(1)}%\n" +
                "chance that attack will inflict mortal wounds: ${(result.second * 100).round(1)}%"
    }

    //tools
    private fun d6(param: Int): Double = when (param) {
        in 2..6 -> (6 - (param - 1)).toDouble() / 6
        else -> 0.0
    }

    private fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

}