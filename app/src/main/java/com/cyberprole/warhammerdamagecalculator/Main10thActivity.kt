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
            //TODO: some code
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

        binding.damageSpinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "Selected Damage: ${binding.damageSpinner.adapter.getItem(position)}")
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

        binding.invulnerableSaveSpinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "Selected Invulnerable Save: ${binding.invulnerableSaveSpinner.adapter.getItem(position)}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "Invulnerable Save was not selected!")
            }
        })

        binding.feelNoPainSpinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "Selected FNP: ${binding.feelNoPainSpinner.adapter.getItem(position)}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "FNP was not selected!")
            }
        })
    }
}