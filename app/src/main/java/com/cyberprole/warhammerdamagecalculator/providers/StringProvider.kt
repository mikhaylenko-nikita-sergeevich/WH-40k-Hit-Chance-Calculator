package com.cyberprole.warhammerdamagecalculator.providers

import android.content.Context
import androidx.annotation.ArrayRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StringProvider @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getStrings(@ArrayRes arrayId: Int): Array<String> {
        return context.resources.getStringArray(arrayId)
    }
}