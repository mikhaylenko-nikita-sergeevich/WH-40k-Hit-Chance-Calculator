package com.cyberprole.warhammerdamagecalculator.main

import javax.inject.Inject

class DamageCalculatorUseCase @Inject constructor() {

    fun calculateDamage(
        wsbs: String,
        strength: String,
        toughness: String,
        ap: String,
        savet: String,
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
    ): String {
        var hnc = hitNonCriticalChance(wsbs)
        var hc = hitCriticalChance(wsbs)
        var wnc = woundNonCriticalChance(strength, toughness, towoundImprove, towoundDecrease)
        var wc = woundCriticalChance()
        val save = saveFailedChance(ap, savet, invulnerableSave, isCover)

        var hr = 1.0
        var wr = 1.0

        //re-rolls, full re-roll is better, that re-rolls of "1"
        if (wsbs != "N/A") {
            if (isToHitRerollOf1) hr = 1 + 1.0 / 6
            if (isToHitRerollFull) hr = 1 + (1 - (hnc + hc))
        }

        if (isToWoundRerollOf1) wr = 1 + 1.0 / 6
        if (isToWoundRerollFull) wr = 1 + (1 - (wnc + wc))

        //apply re-rolls multipliers
        hnc *= hr
        hc *= hr

        wnc *= wr
        wc *= wr

        //<basic damage, mortal wounds>
        var result: Pair<Double, Double> = when {

            !lethalHits && devastatingWounds -> Pair(
                hnc * wnc * save + hc * wnc * save,
                hnc * wc * 1 + hc * wc * 1
            )

            lethalHits && !devastatingWounds -> Pair(
                hnc * wnc * save + hnc * wc * save + hc * 1 * save + hc * 1 * save,
                0.0
            )

            lethalHits && devastatingWounds -> Pair(
                hnc * wnc * save + hc * 1 * save + hc * 1 * save,
                hnc * wc * 1
            )

            else -> Pair(//базовый вариант без модификаторов
                (hnc * wnc * save) + (hnc * wc * save) + (hc * wnc * save) + (hc * wc * save),
                0.0
            )
        }

        val fnp = fnp(feelNoPain)
        result = Pair(result.first, result.second * (1.0 - fnp))
        if (!isFnpAgainstMortalWoundsOnly) {
            result = Pair(result.first * (1.0 - fnp), result.second)
        }

        return "Successfull hit chance: ${((hnc + hc) * 100).round(1)}%\n" +
                "Successfull wound chance: ${((wnc + wc) * 100).round(1)}%\n" +
                "Successfull save chance: ${((1 - save) * 100).round(1)}%\n" +
                "Successfull FNP chance: ${(fnp * 100).round(1)}\n\n" +
                "Chance of inflicting normal damage: ${(result.first * 100).round(1)}%\n" +
                "Chance of inflicting damage like mortal wounds: ${(result.second * 100).round(1)}%"
    }

    private fun hitNonCriticalChance(wsbs: String): Double {
        if (wsbs == "N/A")
            return 1.0
        else {
            val firstSymbol = wsbs[0]
            val param = Integer.parseInt(firstSymbol.toString())

            val successRollChance = d6(param)

            return successRollChance - hitCriticalChance(wsbs)
        }
    }

    private fun hitCriticalChance(wsbs: String): Double {
        return if (wsbs == "N/A") 0.0
        else 1.0 / 6
    }

    private fun woundNonCriticalChance(
        strength: String,
        toughness: String,
        toWoundImprove: Boolean,
        toWoundDecrease: Boolean
    ): Double {
        val ds: Double =
            Integer.parseInt(strength).toDouble()
        val dt: Double =
            Integer.parseInt(toughness).toDouble()

        var param = when {
            ds <= dt / 2 -> 6
            ds < dt -> 5
            ds == dt -> 4
            ds < dt * 2 -> 3
            ds >= dt * 2 -> 2
            else -> throw Exception("PARAM INVALID")
        }

        //add +1 to wound roll
        if (toWoundImprove && param in 3..6) param -= 1
        if (toWoundDecrease && param in 2..5) param += 1

        val successRollChance = d6(param)
        return successRollChance - woundCriticalChance()
    }

    private fun woundCriticalChance(): Double {
        return 1.0 / 6
    }

    private fun saveFailedChance(
        apt: String,
        savet: String,
        invulnerableSavet: String,
        isCover: Boolean
    ): Double {
        //AP — отрицательная величина, на неё ухудшается базовый сейв
        val ap = Integer.parseInt(apt)
        val save = Integer.parseInt(savet[0].toString())
        val invString = invulnerableSavet

        val inv = when (invString) {
            "-" -> 7
            else -> Integer.parseInt(invString[0].toString())
        }

        var modifiedSave = save
        if (isCover && (save > 3 || ap < 0)) modifiedSave = modifiedSave - ap - 1
        else modifiedSave = modifiedSave - ap

        //выбираем лучший показатель между обычным сейвом и инвулём
        val saveChance = when {
            modifiedSave <= inv -> d6(modifiedSave)
            else -> d6(inv)
        }

        return 1.0 - saveChance
    }

    private fun fnp(feelNoPain: String): Double {
        if (feelNoPain != "-") {
            val param = Integer.parseInt(feelNoPain[0].toString())
            return d6(param)
        } else return 0.0
    }

    //tools
    private fun d6(param: Int): Double = when (param) {
        in 2..6 -> (6 - (param - 1)).toDouble() / 6
        else -> 0.0
    }

    private fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
}