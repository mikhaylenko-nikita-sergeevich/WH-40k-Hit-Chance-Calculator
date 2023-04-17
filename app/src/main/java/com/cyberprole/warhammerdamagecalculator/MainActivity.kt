package com.cyberprole.warhammerdamagecalculator

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cyberprole.warhammerdamagecalculator.databinding.ActivityMainBinding
import java.text.DecimalFormat

private val TAG = MainActivity::class.simpleName

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            buttonCalculate.setOnClickListener {
                try {
                    //parse
                    val wsbs = editTextNumberWSBS.parse()
                    val strength = editTextNumberStrength.parse()
                    val ap = editTextNumberAP.parse()
                    val damage = editTextNumberDamage.parse()

                    val toughness = editTextNumberToughness.parse()
                    val save = editTextNumberSave.parse()
                    val inv = editTextNumberInvulnerableSave.parse()
                    val fnp = editTextNumberFeelNoPain.parse()

                    val attacks = editTextNumberAttacks.parse()

                    //todo: validation

                    //calculating!
                    val hit_step = d6chance(wsbs)
                    val wound_step = woundChance(strength, toughness)
                    val save_step = 1 - saveChance(save, inv, ap)
                    val fnp_step = 1 - fnpChance(fnp)

                    val result = hit_step * wound_step * save_step * fnp_step

                    textViewResult.text =
                        "Result:\n" +
                                "Вероятность прохождения одной атаки:${result.formatPercent()}\n" +
                                "Средний статистический урон: ${(attacks * result * damage).round()}"
                } catch (exception: NumberFormatException) {
                    Toast.makeText(
                        applicationContext,
                        "Errro! ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }
        }
    }

    private fun d6chance(param: Int): Double = when (param) {
        in 2..6 -> (6 - (param - 1)).toDouble() / 6
        else -> 1.0
    }

    private fun woundChance(s: Int, t: Int): Double {
        val ds: Double = s.toDouble()
        val dt: Double = t.toDouble()

        return when {
            ds <= dt / 2 -> d6chance(6)
            ds < dt -> d6chance(5)
            ds == dt -> d6chance(4)
            ds < dt * 2 -> d6chance(3)
            ds >= dt * 2 -> d6chance(2)
            else -> throw Exception("PARAM INVALID")
        }
    }

    private fun saveChance(save: Int, inv: Int, ap: Int): Double = when {
        inv in 2..6 -> d6chance(inv)
        save in 2..6 -> d6chance(save + ap)
        else -> 0.0
    }

    private fun fnpChance(fnp: Int): Double = when (fnp) {
        in 2..6 -> d6chance(fnp)
        else -> 0.0
    }

    private fun Double.formatPercent() = DecimalFormat("0.00%").format(this)

    private fun Double.round() = DecimalFormat("0.00").format(this)

    private fun EditText.parse() = Integer.parseInt(text.toString())

}