package com.pdm.esas.ui.calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class ActivityItem(
    val title: String,
    val time: String,
    val description: String,
    val spotsTaken: Int,
    val spotsTotal: Int,
    val isEnrolled: Boolean
)
// #TODO  imlementar separador e fazer com a parte referente a cards ocupe todo o espaco de tela ..., aumentar o tamanho do titulo e diminuir o tamanho do botao , aumentar horario
@Composable
fun ActivityCard(
    activity: ActivityItem,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium // Forma padrão
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateContentSize(),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Título e Horário (com horário opcional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF144476)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (activity.time.isNotEmpty()) {
                    Text(
                        text = activity.time,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Descrição expandível
            if (isExpanded && activity.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Informações de vagas e botão
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Texto de Vagas
                val spotsColor = if (activity.spotsTaken < activity.spotsTotal) {
                    MaterialTheme.colorScheme.tertiary // Cor verde
                } else {
                    MaterialTheme.colorScheme.error // Cor vermelha
                }
                Text(
                    text = "Vagas: ${activity.spotsTaken} de ${activity.spotsTotal}",
                    style = MaterialTheme.typography.labelLarge.copy(color = spotsColor),
                    modifier = Modifier
                        .weight(1f) // Ocupa o mesmo espaço que o botão
                        .padding(end = 8.dp)
                )

                // Botão
                Button(
                    onClick = { /* Lógica de inscrição */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activity.isEnrolled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp), // Levemente arredondado
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp), // Adiciona profundidade
                    modifier = Modifier
                        .weight(1f) // Ocupa o mesmo espaço que o texto de vagas
                        .height(44.dp) // Altura fixa
                ) {
                    Text(
                        text = if (activity.isEnrolled) "Desinscrever" else "Inscrever",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Link para descrição (se existir)
            if (activity.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isExpanded) "Ver menos" else "Ver descrição",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
