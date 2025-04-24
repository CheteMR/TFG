package com.example.connex_jetpack.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

// ⚠️ Estado global PROVISIONAL. Reemplazar con ViewModel más adelante.
var isEmpresaGlobal: MutableState<Boolean> = mutableStateOf(false)