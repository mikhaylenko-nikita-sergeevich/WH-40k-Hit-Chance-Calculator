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

    private fun calculateDamage() {
        val hitChance = hitChance()
        var woundChance = woundChance()
        val throughSaveChance = 1.0 - saveChance()
        var throughFNPChance = 1.0

        val fnpParamString = binding.feelNoPainSpinner.selectedItem.toString()[0].toString()
        if (fnpParamString != "-") {
            val fnpParam = Integer.parseInt(fnpParamString)

            if (binding.fnpAgainstMortalWoundsOnlyCheckbox.isChecked) {
                //только морталки от DW
            } else {
                //всё
                throughFNPChance = 1 - d6(fnpParam)
            }
        }

        var throughWoundChance = hitChance * woundChance

        if (binding.lethalHitsCheckbox.isChecked) {
            if (hitChance == 1.0) {
                //только у оружия с WS/BS == N/A типа огнемётов
                //на такое оружие LH не влияет
            } else {
                //шанс попадания, но не критического: hitChance - 1.0/6
                //ФОРМУЛА БЕЗ РЕРОЛОВ НА ТУХИТ
                //todo: разделить tohit() на fail,non-critical,critical изаменить 1/6 на critical
                throughWoundChance = (hitChance - 1.0 / 6) * woundChance + 1.0 / 6
            }
        }
        if (binding.devastatingWoundsCheckbox.isChecked) {
            //todo
        }
        if (binding.sustainedHits1Checkbox.isChecked) {
            //todo
        }

        binding.resultTextview.text = "Result:\n" +
                "hit chance: ${(hitChance * 100).round(1)}%\n" +
                "wound chance: ${(woundChance * 100).round(1)}%\n" +
                "through wound chance: ${(throughWoundChance * 100).round(1)}%\n" +
                "through save chance: ${(throughSaveChance * 100).round(1)}%\n" +
                "through FNP chance: ${(throughFNPChance * 100).round(1)}%\n" +
                "result chance: ${
                    (throughWoundChance * throughSaveChance * throughFNPChance * 100).round(
                        1
                    )
                }%"
    }

    private fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

    private fun hitChance(): Double {
        when (binding.wsbsSpinner.selectedItem) {
            "N/A" -> return 1.0
            else -> {
                val firstSymbol = binding.wsbsSpinner.selectedItem.toString()[0]
                val param = Integer.parseInt(firstSymbol.toString())
                val successRollChance = d6(param)
                var result = successRollChance

                //todo: проверить, что можно несколько раз реролить за разные правила
                if (binding.tohitRerollFullCheckbox.isChecked) {
                    //todo: проверить, что вероятности реально так складываются
                    result += (1.0 - successRollChance) * successRollChance
                }
                if (binding.tohitRerollOf1Checkbox.isChecked) {
                    result += 1.0 / 6 * successRollChance
                }
                if (binding.tohitReroll1DiceCheckbox.isChecked) {
                    //todo
                }
                return result
            }
        }
    }

    private fun woundChance(): Double {
        val ds: Double = Integer.parseInt(binding.strengthEdittext.text.toString()).toDouble()
        val dt: Double = Integer.parseInt(binding.toughnessEdittext.text.toString()).toDouble()

        val param = when {
            ds <= dt / 2 -> 6
            ds < dt -> 5
            ds == dt -> 4
            ds < dt * 2 -> 3
            ds >= dt * 2 -> 2
            else -> throw Exception("PARAM INVALID")
        }

        val successRollChance = d6(param)
        var result = successRollChance

        if (binding.towoundRerollOf1Checkbox.isChecked) {
            //шанс, что в результате одного(!) броска выпадет единица - 1/6, и тогда совершим рерол
            result += 1.0 / 6 * successRollChance
        }
        if (binding.towoundRerollFullCheckbox.isChecked) {
            //result — шанс успешного первого броска
            //1 - result — шанс неуспешного первого броска
            //(1 - result) * result — шанс, что первый бросок будет неудачный, мы сделаем второй и он будет удачный
            //а теперь складываем обе вероятности
            result += (1 - successRollChance) * successRollChance
        }
        if (binding.towoundReroll1DiceCheckbox.isChecked) {
            //todo:
        }

        return result
    }

    private fun saveChance(): Double {
        //AP — отрицательная величина, на неё ухудшается базовый сейв
        val ap = Integer.parseInt(binding.apSpinner.selectedItem.toString())
        val save = Integer.parseInt(binding.saveSpinner.selectedItem.toString()[0].toString())
        val invString = binding.invulnerableSaveSpinner.selectedItem.toString()[0].toString()
        val inv = when (invString) {
            "-" -> 7
            else -> Integer.parseInt(invString)
        }
        //val fnp = Integer.parseInt(binding.feelNoPainSpinner.selectedItem.toString())

        //todo: добавить ковер
        val isCover = false
        val modifiedSave = when (isCover) {
            true -> save - ap - 1
            false -> save - ap
        }

        //выбираем лучший показатель между эбычным сейвом и инвулём
        val chance = when {
            modifiedSave <= inv -> d6(modifiedSave)
            else -> d6(inv)
        }

        return chance
    }

    private fun d6(param: Int): Double = when (param) {
        in 2..6 -> (6 - (param - 1)).toDouble() / 6
        else -> 0.0
    }
}