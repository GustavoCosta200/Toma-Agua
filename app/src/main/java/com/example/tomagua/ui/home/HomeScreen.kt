package com.example.tomagua.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tomagua.data.local.entity.ConfigurationReminder
import com.example.tomagua.ui.theme.extraColors

@Composable
fun HomeScreen(
    onNavigateToProfiles: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            uiState.activeProfile == null -> EmptyProfileState(onNavigateToProfiles)
            else -> HomeContent(
                profileName = uiState.activeProfile!!.name,
                consumedMl = uiState.consumedTodayMl,
                goalMl = uiState.goalMl,
                progress = uiState.progress,
                reminders = uiState.activeReminders,
                onRegister = { reminder ->
                    viewModel.registerConsumption(reminder.id, reminder.mlQuantity)
                }
            )
        }
    }
}

@Composable
private fun HomeContent(
    profileName: String,
    consumedMl: Int,
    goalMl: Int,
    progress: Float,
    reminders: List<ConfigurationReminder>,
    onRegister: (ConfigurationReminder) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = profileName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        HydrationRing(
            progress = progress,
            consumedMl = consumedMl,
            goalMl = goalMl,
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Registrar consumo",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (reminders.isEmpty()) {
            Text(
                text = "Nenhum lembrete configurado para este perfil ainda.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(reminders, key = { it.id }) { reminder ->
                    ReminderChip(reminder = reminder, onClick = { onRegister(reminder) })
                }
            }
        }
    }
}

@Composable
private fun HydrationRing(
    progress: Float,
    consumedMl: Int,
    goalMl: Int,
    modifier: Modifier = Modifier
) {
    val ringStart = MaterialTheme.colorScheme.primary
    val ringEnd = MaterialTheme.extraColors.ringGradientEnd
    val ringTrack = MaterialTheme.extraColors.ringTrack
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textMuted = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 20.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            drawArc(
                color = ringTrack,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                brush = Brush.sweepGradient(listOf(ringStart, ringEnd, ringStart)),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$consumedMl",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Text(
                text = "de $goalMl ml",
                style = MaterialTheme.typography.bodyMedium,
                color = textMuted
            )
        }
    }
}

@Composable
private fun ReminderChip(
    reminder: ConfigurationReminder,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "+${reminder.mlQuantity} ml",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        if (reminder.message.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = reminder.message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyProfileState(onNavigateToProfiles: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = "Nenhum perfil ativo",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ative um perfil para acompanhar seu consumo de água hoje.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onNavigateToProfiles) {
            Text("Ver perfis")
        }
    }
}