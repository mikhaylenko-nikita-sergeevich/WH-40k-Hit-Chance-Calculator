package com.cyberprole.warhammerdamagecalculator.main.usecases

import javax.inject.Inject

data class InputData(
    val wsbs: Int?,//у огнемётов нет этого параметра, там автоматическое попадание
    val strength: Int,
    val toughness: Int,
    val ap: Int,
    val save: Int,
    val invulnerableSave: Int?,
    val feelNoPain: Int?,
    val isLethalHits: Boolean,
    val isDevastatingWounds: Boolean,
    val isToWoundImprove: Boolean,
    val isToWoundDecrease: Boolean,
    val isCover: Boolean,
    val isToHitReRollOf1: Boolean,
    val isToHitReRollFull: Boolean,
    val isToWoundReRollOf1: Boolean,
    val isToWoundReRollFull: Boolean,
    val isFnpAgainstMortalWoundsOnly: Boolean
)

data class OutputData(
    val hitChance: Double,
    val woundChance: Double,
    val saveChance: Double,
    val fnpChance: Double,
    val chanceOfInflictingNormalDamage: Double,
    val chanceOfInflictingDamageLikeMortalWounds: Double
)

class DamageCalculatorUseCase @Inject constructor() {

    fun calculateDamage(input: InputData): OutputData {
        var hnc = hitNonCriticalChance(input.wsbs)
        var hc = hitCriticalChance(input.wsbs)
        var wnc = woundNonCriticalChance(
            input.strength,
            input.toughness,
            input.isToWoundImprove,
            input.isToWoundDecrease
        )
        var wc = woundCriticalChance()
        val save = saveFailedChance(
            input.ap,
            input.save,
            input.invulnerableSave,
            input.isCover
        )

        var hr = 1.0
        var wr = 1.0

        //re-rolls, full re-roll is better, that re-rolls of "1"
        if (input.wsbs != -1) {
            if (input.isToHitReRollOf1) hr = 1 + 1.0 / 6
            if (input.isToHitReRollFull) hr = 1 + (1 - (hnc + hc))
        }

        if (input.isToWoundReRollOf1) wr = 1 + 1.0 / 6
        if (input.isToWoundReRollFull) wr = 1 + (1 - (wnc + wc))

        //apply re-rolls multipliers
        hnc *= hr
        hc *= hr

        wnc *= wr
        wc *= wr

        //<basic damage, mortal wounds>
        var result: Pair<Double, Double> = when {

            !input.isLethalHits && input.isDevastatingWounds -> Pair(
                hnc * wnc * save + hc * wnc * save,
                hnc * wc * 1 + hc * wc * 1
            )

            input.isLethalHits && !input.isDevastatingWounds -> Pair(
                hnc * wnc * save + hnc * wc * save + hc * 1 * save + hc * 1 * save,
                0.0
            )

            input.isLethalHits && input.isDevastatingWounds -> Pair(
                hnc * wnc * save + hc * 1 * save + hc * 1 * save,
                hnc * wc * 1
            )

            else -> Pair(//базовый вариант без модификаторов
                (hnc * wnc * save) + (hnc * wc * save) + (hc * wnc * save) + (hc * wc * save),
                0.0
            )
        }

        val fnp = fnp(input.feelNoPain)
        result = Pair(result.first, result.second * (1.0 - fnp))
        if (!input.isFnpAgainstMortalWoundsOnly) {
            result = Pair(result.first * (1.0 - fnp), result.second)
        }

        return OutputData(
            hitChance = (hnc + hc),
            woundChance = (wnc + wc),
            saveChance = (1 - save),
            fnpChance = fnp,
            chanceOfInflictingNormalDamage = result.first,
            chanceOfInflictingDamageLikeMortalWounds = result.second
        )
    }

    private fun hitNonCriticalChance(wsbs: Int?): Double {
        if (wsbs != null) {
            val successRollChance = d6(wsbs)
            return successRollChance - hitCriticalChance(wsbs)
        } else {
            return 1.0
        }
    }

    private fun hitCriticalChance(wsbs: Int?): Double {
        return if (wsbs != null) 1.0 / 6
        else 0.0
    }

    private fun woundNonCriticalChance(
        strength: Int,
        toughness: Int,
        toWoundImprove: Boolean,
        toWoundDecrease: Boolean
    ): Double {
        val ds: Double = strength.toDouble()
        val dt: Double = toughness.toDouble()

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
        ap: Int,
        save: Int,
        invulnerableSave: Int?,
        isCover: Boolean
    ): Double {
        var modifiedSave = save
        modifiedSave = when {
            isCover && (save > 3 || ap < 0) -> modifiedSave - ap - 1
            else -> modifiedSave - ap
        }

        //выбираем лучший показатель между обычным сейвом и инвулём
        val saveChance = when {
            (invulnerableSave == null || modifiedSave <= invulnerableSave) -> d6(modifiedSave)
            else -> d6(invulnerableSave)
        }

        return 1.0 - saveChance
    }

    private fun fnp(feelNoPain: Int?): Double {
        return if (feelNoPain != null) d6(feelNoPain)
        else 0.0
    }

    //tools
    private fun d6(param: Int): Double = when (param) {
        in 2..6 -> (6 - (param - 1)).toDouble() / 6
        else -> 0.0
    }
}