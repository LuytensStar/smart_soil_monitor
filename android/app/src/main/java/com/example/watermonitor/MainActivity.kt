package com.example.watermonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.watermonitor.ui.theme.WaterMonitorTheme


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaterMonitorTheme {

                val vm: MainViewModel = viewModel(
                    factory = ViewModelFactory("http://192.168.0.4:8000/")
                )
                val state by vm.state.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {

                        TopAppBar(
                            title = { Text("Plant Monitor") }
                        )
                    }
                ) { inner ->
                    Dashboard(
                        modifier = Modifier
                            .padding(inner)
                            .fillMaxSize(),
                        brightness = state.brightness,
                        moisture = state.moisture
                    )
                }
            }
        }
    }
}



@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    brightness: Int,
    moisture: Int
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SensorCard(
            icon = Icons.Filled.Lightbulb,
            label = "Brightness",
            value = brightness,
            barColor = MaterialTheme.colorScheme.tertiary
        )
        SensorCard(
            icon = Icons.Filled.WaterDrop,
            label = "Moisture",
            value = moisture,
            barColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SensorCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: Int,
    barColor: androidx.compose.ui.graphics.Color
) {

    val animated = animateFloatAsState(value / 100f, label = "anim")
    val alertColor = MaterialTheme.colorScheme.error
    val tint = if (value < 30) alertColor else barColor

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null)
                Spacer(Modifier.width(8.dp))
                Text(label, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text("$value%", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { animated.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                color = tint,
            )
        }
    }
}
