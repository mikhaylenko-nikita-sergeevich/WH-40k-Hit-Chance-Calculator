package com.cyberprole.warhammerdamagecalculator.main.presentation

data class AttackerState(
    var wsbs: String = "3+",
    var strength: String = "4",
    var ap: String = "-1",
    var lethalHits: Boolean = false,
    var devastatingWounds: Boolean = false,
    var toHitReRollOf1: Boolean = false,
    var toHitReRollFull: Boolean = false,
    var toWoundReRollOf1: Boolean = false,
    var toWoundReRollFull: Boolean = false,
    var toWoundImprove: Boolean = false
)

data class DefenderState(
    var toughness: String = "4",
    var save: String = "4+",
    var invulnerableSave: String = "7+",
    var feelNoPain: String = "7+",
    var fnpAgainstMortalWoundsOnly: Boolean = false,
    var toWoundDecrease: Boolean = false,
    var cover: Boolean = false
)