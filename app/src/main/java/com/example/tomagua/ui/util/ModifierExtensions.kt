package com.example.tomagua.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.clickableSimple(onClick: () -> Unit): Modifier =
    this.then(clickable(onClick = onClick))