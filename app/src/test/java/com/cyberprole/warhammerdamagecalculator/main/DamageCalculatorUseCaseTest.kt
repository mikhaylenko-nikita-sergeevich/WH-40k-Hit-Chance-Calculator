package com.cyberprole.warhammerdamagecalculator.main

import com.cyberprole.warhammerdamagecalculator.main.usecases.DamageCalculatorUseCase
import com.cyberprole.warhammerdamagecalculator.main.usecases.InputData
import com.cyberprole.warhammerdamagecalculator.main.usecases.OutputData
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DamageCalculatorUseCaseTest {

    private val damageCalculator = DamageCalculatorUseCase()

    @Test
    fun `test flamer`() {
        val inputData = InputData(
            wsbs = null,
            strength = 3,
            toughness = 3,
            ap = 0,
            save = 6,
            invulnerableSave = 4,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 0.5,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 0.25,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }

    @Test
    fun `test bolter`() {
        val inputData = InputData(
            wsbs = 3,
            strength = 4,
            toughness = 4,
            ap = -1,
            save = 3,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 0.6666666666666666,
            woundChance = 0.5,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 0.16666666666666669,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }

    @Test
    fun `test wound 1 to 4`() {
        val inputData = InputData(
            wsbs = null,
            strength = 1,
            toughness = 4,
            ap = 0,
            save = 4,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 1.0 / 6.0,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 0.5/6.0,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }

    @Test
    fun `test wound 2 to 4`() {
        val inputData = InputData(
            wsbs = null,
            strength = 2,
            toughness = 4,
            ap = 0,
            save = 4,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 1.0 / 6.0,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 0.5/6.0,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }

    @Test
    fun `test wound 3 to 4`() {
        val inputData = InputData(
            wsbs = null,
            strength = 3,
            toughness = 4,
            ap = 0,
            save = 4,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 2.0 / 6.0,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 1.0/6.0,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }

    @Test
    fun `test wound 4 to 4`() {
        val inputData = InputData(
            wsbs = null,
            strength = 4,
            toughness = 4,
            ap = 0,
            save = 4,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 3.0/6.0,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 0.25,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }

    @Test
    fun `test wound 5 to 4`() {
        val inputData = InputData(
            wsbs = null,
            strength = 5,
            toughness = 4,
            ap = 0,
            save = 4,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 4.0 / 6.0,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 1.0 / 3.0,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }

    @Test
    fun `test wound 6 to 4`() {
        val inputData = InputData(
            wsbs = null,
            strength = 6,
            toughness = 4,
            ap = 0,
            save = 4,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 4.0 / 6.0,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 1.0 / 3.0,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }

    @Test
    fun `test wound 7 to 4`() {
        val inputData = InputData(
            wsbs = null,
            strength = 7,
            toughness = 4,
            ap = 0,
            save = 4,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 4.0 / 6.0,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 1.0 / 3.0,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }
    @Test
    fun `test wound 8 to 4`() {
        val inputData = InputData(
            wsbs = null,
            strength = 8,
            toughness = 4,
            ap = 0,
            save = 4,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 5.0/6.0,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 2.5/6.0,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }
    @Test
    fun `test wound 9 to 4`() {
        val inputData = InputData(
            wsbs = null,
            strength = 9,
            toughness = 4,
            ap = 0,
            save = 4,
            invulnerableSave = null,
            feelNoPain = null,
            isLethalHits = false,
            isDevastatingWounds = false,
            isToWoundImprove = false,
            isToWoundDecrease = false,
            isCover = false,
            isToHitReRollOf1 = false,
            isToHitReRollFull = false,
            isToWoundReRollOf1 = false,
            isToWoundReRollFull = false,
            isFnpAgainstMortalWoundsOnly = false
        )
        val outputData = OutputData(
            hitChance = 1.0,
            woundChance = 5.0/6.0,
            saveChance = 0.5,
            fnpChance = 0.0,
            chanceOfInflictingNormalDamage = 2.5/6.0,
            chanceOfInflictingDamageLikeMortalWounds = 0.0
        )

        assertEquals(damageCalculator.calculateDamage(inputData), outputData)
    }
}