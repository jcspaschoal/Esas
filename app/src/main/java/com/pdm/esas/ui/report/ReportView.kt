package com.pdm.esas.ui.report

import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.pdm.esas.data.models.VisitWithVisitor
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReportView(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val nationalityCountsState = remember { mutableStateOf<Map<String, Int>?>(null) }
    val errorState = remember { mutableStateOf<String?>(null) }
    val visitDatesState = remember { mutableStateOf<List<VisitWithVisitor>?>(null) }

    LaunchedEffect(Unit) {
        val nationalityResult = viewModel.countNationalities()
        if (nationalityResult.isSuccess) {
            nationalityCountsState.value = nationalityResult.getOrNull()
        } else {
            errorState.value = nationalityResult.exceptionOrNull()?.message
        }

        val visitResult = viewModel.countVisits()
        if (visitResult.isSuccess) {
            visitDatesState.value = visitResult.getOrNull()
        } else {
            errorState.value = visitResult.exceptionOrNull()?.message
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item {
            Text(
                text = "Relatórios",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }


        item {
            Text(
                text = "Distribuição de Nacionalidades",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Exibe erros, se houver
        errorState.value?.let { error ->
            item {
                Text(
                    text = "Erro: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        nationalityCountsState.value?.let { nationalityCounts ->
            if (nationalityCounts.isNotEmpty()) {
                item {
                    CustomPieChart(
                        data = nationalityCounts.map { (label, value) -> label to value },
                        modifier = Modifier
                            .height(350.dp)
                            .padding(16.dp)
                    )
                }
            } else {
                item {
                    Text(
                        text = "Sem dados de nacionalidades.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        visitDatesState.value?.let { visitsWithVisitors ->
            if (visitsWithVisitors.isNotEmpty()) {
                item {
                    Text(
                        text = "Datas das Visitas",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                    )
                }

                // Ordena (mais recentes primeiro)
                val sortedVisits = visitsWithVisitors.sortedByDescending { it.visit.date }
                items(sortedVisits) { visitWithVisitor ->
                    val visit = visitWithVisitor.visit
                    val visitor = visitWithVisitor.visitor

                    // Capitaliza o primeiro caractere do nome, se existir
                    val capitalizedName = visitor?.name?.replaceFirstChar { it.uppercase() }
                        ?: "Desconhecido"

                    // Formata a data no padrão HH:mm dd/MM/yyyy
                    val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
                    val dateString = visit.date?.toDate()?.let { dateFormat.format(it) }
                        ?: "Data desconhecida"

                    Text(
                        text = "Visitante: $capitalizedName em $dateString",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                item {
                    Text(
                        text = "Nenhuma visita disponível.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } ?: item {
            Text(
                text = "A carregar visitas...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Composable que exibe um PieChart do MPAndroidChart usando valores percentuais.
 */
@Composable
fun CustomPieChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PieChart(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                setUsePercentValues(true) // Exibe como percentuais
                description = Description().apply { text = "" }
                isDrawHoleEnabled = false
            }
        },
        update = { pieChart ->
            // Somatório para converter os valores em frações (o MPAndroidChart faz isso se setUsePercentValues(true))
            val total = data.sumOf { it.second }

            // Cria entradas (ainda em valores absolutos, mas o setUsePercentValues(true) converte para %)
            val entries = data.map { (label, value) ->
                PieEntry(value.toFloat(), label)
            }

            // Gerando cores distintas para cada fatia
            val dataSet = PieDataSet(entries, "Distribuição").apply {
                // Função auxiliar para gerar cores distintas
                colors = generateDistinctColorIntArray(data.size).toList()
                valueTextSize = 14f
                // Exibe o texto em formato de percentagem
                valueFormatter = PercentFormatter(pieChart)
            }

            val pieData = PieData(dataSet)
            pieData.setValueTextSize(14f)

            pieChart.data = pieData
            pieChart.invalidate() // Redesenha o gráfico
        }
    )
}


fun generateDistinctColorIntArray(size: Int): IntArray {
    val array = IntArray(size)
    for (i in 0 until size) {
        val hue = (i * 360f / size) % 360
        // Saturação e brilho ajustados para tons mais vivos
        val color = android.graphics.Color.HSVToColor(floatArrayOf(hue, 0.9f, 0.9f))
        array[i] = color
    }
    return array
}
