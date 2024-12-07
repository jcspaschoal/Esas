package com.pdm.esas.ui.report

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReportView(modifier: Modifier = Modifier,  viewModel: ReportViewModel = hiltViewModel()) {
    Text(text = "Tela Report")
}