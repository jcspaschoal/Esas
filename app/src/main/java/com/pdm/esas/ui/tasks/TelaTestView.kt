@file:JvmName("TelaTestVIewModelKt")

package com.pdm.esas.ui.tasks

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TaskView(modifier: Modifier = Modifier,
             viewModel: TelaTestViewModel = hiltViewModel() ) {
    Text(text = "Tela Task")
}