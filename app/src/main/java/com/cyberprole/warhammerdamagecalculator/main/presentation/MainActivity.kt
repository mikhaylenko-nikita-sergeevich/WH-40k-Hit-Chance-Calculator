package com.cyberprole.warhammerdamagecalculator.main.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyberprole.warhammerdamagecalculator.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WarhammerDamageCalculatorTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val attackerState = remember { mutableStateOf(AttackerState()) }
    val defenderState = remember { mutableStateOf(DefenderState()) }

    var strengthError by remember { mutableStateOf<String?>(null) }
    var toughnessError by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AttackerCard(attackerState)
            Spacer(modifier = Modifier.height(16.dp))
            DefenderCard(defenderState)
            Spacer(modifier = Modifier.height(16.dp))
            ResultCard(uiState)
            Spacer(modifier = Modifier.height(16.dp))
            CalculateButton(uiState) {
                strengthError = if (attackerState.value.strength.toIntOrNull() == null) "Invalid number" else null
                toughnessError = if (defenderState.value.toughness.toIntOrNull() == null) "Invalid number" else null

                if (strengthError == null && toughnessError == null) {
                    viewModel.onCalculateClicked(
                        wsbs = attackerState.value.wsbs,
                        strength = attackerState.value.strength,
                        toughness = defenderState.value.toughness,
                        ap = attackerState.value.ap,
                        save = defenderState.value.save,
                        invulnerableSave = defenderState.value.invulnerableSave,
                        feelNoPain = defenderState.value.feelNoPain,
                        isLethalHits = attackerState.value.lethalHits,
                        isDevastatingWounds = attackerState.value.devastatingWounds,
                        isToWoundImprove = attackerState.value.toWoundImprove,
                        isToWoundDecrease = defenderState.value.toWoundDecrease,
                        isCover = defenderState.value.cover,
                        isToHitReRollOf1 = attackerState.value.toHitReRollOf1,
                        isToHitReRollFull = attackerState.value.toHitReRollFull,
                        isToWoundReRollOf1 = attackerState.value.toWoundReRollOf1,
                        isToWoundReRollFull = attackerState.value.toWoundReRollFull,
                        isFnpAgainstMortalWoundsOnly = defenderState.value.fnpAgainstMortalWoundsOnly
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttackerCard(attackerState: MutableState<AttackerState>) {
    val wsbsOptions = listOf("N/A", "2+", "3+", "4+", "5+", "6+")
    var wsbsExpanded by remember { mutableStateOf(false) }
    val apOptions = listOf("0", "-1", "-2", "-3", "-4")
    var apExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = R.string.card_title_attacker))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = wsbsExpanded,
                    onExpandedChange = { wsbsExpanded = !wsbsExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        readOnly = true,
                        value = attackerState.value.wsbs,
                        onValueChange = { },
                        label = { Text(stringResource(id = R.string.textfield_hint_wsbs)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = wsbsExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = wsbsExpanded,
                        onDismissRequest = { wsbsExpanded = false }
                    ) {
                        wsbsOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    attackerState.value = attackerState.value.copy(wsbs = selectionOption)
                                    wsbsExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = attackerState.value.strength,
                    onValueChange = { attackerState.value = attackerState.value.copy(strength = it) },
                    label = { Text(stringResource(id = R.string.textfield_hint_strength)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ExposedDropdownMenuBox(
                    expanded = apExpanded,
                    onExpandedChange = { apExpanded = !apExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        readOnly = true,
                        value = attackerState.value.ap,
                        onValueChange = { },
                        label = { Text(stringResource(id = R.string.textfield_hint_ap)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = apExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = apExpanded,
                        onDismissRequest = { apExpanded = false }
                    ) {
                        apOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    attackerState.value = attackerState.value.copy(ap = selectionOption)
                                    apExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_lethal_hits), checked = attackerState.value.lethalHits, onCheckedChange = { attackerState.value = attackerState.value.copy(lethalHits = it) })
                CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_devastating_wounds), checked = attackerState.value.devastatingWounds, onCheckedChange = { attackerState.value = attackerState.value.copy(devastatingWounds = it) })
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_hit_reroll_of_1), checked = attackerState.value.toHitReRollOf1, onCheckedChange = { attackerState.value = attackerState.value.copy(toHitReRollOf1 = it) })
                CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_hit_full_reroll), checked = attackerState.value.toHitReRollFull, onCheckedChange = { attackerState.value = attackerState.value.copy(toHitReRollFull = it) })
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_wound_reroll_of_1), checked = attackerState.value.toWoundReRollOf1, onCheckedChange = { attackerState.value = attackerState.value.copy(toWoundReRollOf1 = it) })
                CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_wound_full_reroll), checked = attackerState.value.toWoundReRollFull, onCheckedChange = { attackerState.value = attackerState.value.copy(toWoundReRollFull = it) })
            }
            CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_improve_wound_roll), checked = attackerState.value.toWoundImprove, onCheckedChange = { attackerState.value = attackerState.value.copy(toWoundImprove = it) })
        }
    }
}

@Composable
fun CheckboxWithLabel(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefenderCard(defenderState: MutableState<DefenderState>) {
    val saveOptions = listOf("2+", "3+", "4+", "5+", "6+", "7+")
    var saveExpanded by remember { mutableStateOf(false) }
    val invulnerableSaveOptions = listOf("-", "2+", "3+", "4+", "5+", "6+")
    var invulnerableSaveExpanded by remember { mutableStateOf(false) }
    val fnpOptions = listOf("-", "2+", "3+", "4+", "5+", "6+")
    var fnpExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = R.string.card_title_defender))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = defenderState.value.toughness,
                    onValueChange = { defenderState.value = defenderState.value.copy(toughness = it) },
                    label = { Text(stringResource(id = R.string.textfield_hint_toughness)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ExposedDropdownMenuBox(
                    expanded = saveExpanded,
                    onExpandedChange = { saveExpanded = !saveExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        readOnly = true,
                        value = defenderState.value.save,
                        onValueChange = { },
                        label = { Text(stringResource(id = R.string.textfield_hint_save)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = saveExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = saveExpanded,
                        onDismissRequest = { saveExpanded = false }
                    ) {
                        saveOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    defenderState.value = defenderState.value.copy(save = selectionOption)
                                    saveExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = invulnerableSaveExpanded,
                    onExpandedChange = { invulnerableSaveExpanded = !invulnerableSaveExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        readOnly = true,
                        value = defenderState.value.invulnerableSave,
                        onValueChange = { },
                        label = { Text(stringResource(id = R.string.textfield_hint_invulnerable_save)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = invulnerableSaveExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = invulnerableSaveExpanded,
                        onDismissRequest = { invulnerableSaveExpanded = false }
                    ) {
                        invulnerableSaveOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    defenderState.value = defenderState.value.copy(invulnerableSave = selectionOption)
                                    invulnerableSaveExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                ExposedDropdownMenuBox(
                    expanded = fnpExpanded,
                    onExpandedChange = { fnpExpanded = !fnpExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        readOnly = true,
                        value = defenderState.value.feelNoPain,
                        onValueChange = { },
                        label = { Text(stringResource(id = R.string.textfield_hint_feel_no_pain)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = fnpExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = fnpExpanded,
                        onDismissRequest = { fnpExpanded = false }
                    ) {
                        fnpOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    defenderState.value = defenderState.value.copy(feelNoPain = selectionOption)
                                    fnpExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_fnp_against_mortal_wounds_only), checked = defenderState.value.fnpAgainstMortalWoundsOnly, onCheckedChange = { defenderState.value = defenderState.value.copy(fnpAgainstMortalWoundsOnly = it) })
            CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_decrease_to_wound_roll), checked = defenderState.value.toWoundDecrease, onCheckedChange = { defenderState.value = defenderState.value.copy(toWoundDecrease = it) })
            CheckboxWithLabel(label = stringResource(id = R.string.checkbox_text_cover), checked = defenderState.value.cover, onCheckedChange = { defenderState.value = defenderState.value.copy(cover = it) })
        }
    }
}

@Composable
fun ResultCard(uiState: UiState) {
    when (uiState) {
        is UiState.CALCULATED -> {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(id = R.string.card_title_result))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = uiState.result)
                }
            }
        }
        else -> {}
    }
}

@Composable
fun CalculateButton(uiState: UiState, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        enabled = uiState !is UiState.CALCULATING
    ) {
        if (uiState is UiState.CALCULATING) {
            CircularProgressIndicator()
        } else {
            Text(text = stringResource(id = R.string.button_label_calculate))
        }
    }
}

// Basic Theme definition
@Composable
fun WarhammerDamageCalculatorTheme(content: @Composable () -> Unit) {
    val darkTheme = isSystemInDarkTheme()

    val colors = if (darkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}