package com.example.weatherapp.ui.screens.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.data.model.AirQuality
import com.example.weatherapp.data.model.ForecastInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chi tiết thời tiết",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    uiState.weatherInfo?.let { weather ->
                        WeatherDetailCard(weather = weather)
                    }
                }

                if (uiState.forecastList.isNotEmpty()) {
                    item {
                        TemperatureChartCard(forecastList = uiState.forecastList)
                    }

                    item {
                        HourlyTemperatureCard(forecastList = uiState.forecastList)
                    }
                }

                item {
                    WeatherDetailsGrid(weather = uiState.weatherInfo)
                }

                item {
                    uiState.airQuality?.let { airQuality ->
                        AirQualityCard(airQuality = airQuality)
                    }
                }

                item {
                    SunriseSunsetCard(forecastList = uiState.forecastList)
                }
            }
        }
    }
}

@Composable
private fun WeatherDetailCard(
    weather: com.example.weatherapp.data.model.WeatherInfo,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (weather.isDay) {
        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
    } else {
        listOf(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.surface)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(gradientColors))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = weather.cityName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weather.country,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${weather.temperature.toInt()}°C",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weather.conditionText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cảm giác như ${weather.feelsLike.toInt()}°C",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun TemperatureChartCard(
    forecastList: List<ForecastInfo>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Biểu đồ nhiệt độ 5 ngày",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            TemperatureChart(forecastList = forecastList)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                forecastList.forEach { forecast ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = forecast.date.takeLast(5),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TemperatureChart(
    forecastList: List<ForecastInfo>,
    modifier: Modifier = Modifier
) {
    if (forecastList.isEmpty()) return

    val minTemp = forecastList.minOf { it.minTemp }
    val maxTemp = forecastList.maxOf { it.maxTemp }
    val tempRange = maxTemp - minTemp
    val padding = if (tempRange == 0.0) 1.0 else tempRange

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (forecastList.size - 1).coerceAtLeast(1)

        val maxPath = Path()
        val minPath = Path()

        forecastList.forEachIndexed { index, forecast ->
            val x = index * stepX
            val maxY = height - ((forecast.maxTemp - minTemp) / padding * height).toFloat()
            val minY = height - ((forecast.minTemp - minTemp) / padding * height).toFloat()

            if (index == 0) {
                maxPath.moveTo(x, maxY)
                minPath.moveTo(x, minY)
            } else {
                maxPath.lineTo(x, maxY)
                minPath.lineTo(x, minY)
            }
        }

        drawPath(
            path = maxPath,
            color = Color(0xFFFF6B6B),
            style = Stroke(width = 3.dp.toPx())
        )
        drawPath(
            path = minPath,
            color = Color(0xFF4ECDC4),
            style = Stroke(width = 3.dp.toPx())
        )

        forecastList.forEachIndexed { index, forecast ->
            val x = index * stepX
            val maxY = height - ((forecast.maxTemp - minTemp) / padding * height).toFloat()
            val minY = height - ((forecast.minTemp - minTemp) / padding * height).toFloat()

            drawCircle(
                color = Color(0xFFFF6B6B),
                radius = 6.dp.toPx(),
                center = Offset(x, maxY)
            )
            drawCircle(
                color = Color(0xFF4ECDC4),
                radius = 6.dp.toPx(),
                center = Offset(x, minY)
            )
        }
    }

    Row(
        modifier = modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ChartLegendItem(color = Color(0xFFFF6B6B), label = "Cao nhất")
        ChartLegendItem(color = Color(0xFF4ECDC4), label = "Thấp nhất")
    }
}

@Composable
private fun ChartLegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun HourlyTemperatureCard(
    forecastList: List<ForecastInfo>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Nhiệt độ theo giờ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            forecastList.firstOrNull()?.let { today ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    today.hourlyForecast.take(8).forEach { hourly ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = hourly.time.substringAfter(" "),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${hourly.temp.toInt()}°",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherDetailsGrid(
    weather: com.example.weatherapp.data.model.WeatherInfo?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Chi tiết thời tiết",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    icon = Icons.Default.WaterDrop,
                    value = "${weather?.humidity ?: 0}%",
                    label = "Độ ẩm"
                )
                WeatherDetailItem(
                    icon = Icons.Default.Air,
                    value = "${weather?.windSpeed?.toInt() ?: 0} km/h",
                    label = "Tốc độ gió"
                )
                WeatherDetailItem(
                    icon = Icons.Default.Cloud,
                    value = "${weather?.cloud ?: 0}%",
                    label = "Mây che"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    icon = Icons.Default.WbSunny,
                    value = "${weather?.uv ?: 0}",
                    label = "Chỉ số UV"
                )
                WeatherDetailItem(
                    icon = Icons.Default.Visibility,
                    value = "${weather?.windDirection ?: "N/A"}",
                    label = "Hướng gió"
                )
                WeatherDetailItem(
                    icon = Icons.Default.Compress,
                    value = "${weather?.feelsLike?.toInt() ?: 0}°C",
                    label = "Cảm giác"
                )
            }
        }
    }
}

@Composable
private fun WeatherDetailItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun AirQualityCard(
    airQuality: AirQuality,
    modifier: Modifier = Modifier
) {
    val aqiColor = when {
        airQuality.aqiIndex <= 50 -> Color(0xFF4CAF50)
        airQuality.aqiIndex <= 100 -> Color(0xFFFFEB3B)
        airQuality.aqiIndex <= 150 -> Color(0xFFFF9800)
        airQuality.aqiIndex <= 200 -> Color(0xFFFF5722)
        airQuality.aqiIndex <= 300 -> Color(0xFF9C27B0)
        else -> Color(0xFF7B1FA2)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Chất lượng không khí (AQI)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(aqiColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${airQuality.aqiIndex}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = aqiColor
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = airQuality.aqiCategory,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = aqiColor
                    )
                    Text(
                        text = airQuality.aqiDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chỉ số chi tiết",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            AqiDetailRow("PM2.5", airQuality.pm25, "µg/m³")
            AqiDetailRow("PM10", airQuality.pm10, "µg/m³")
            AqiDetailRow("CO", airQuality.co, "µg/m³")
            AqiDetailRow("NO₂", airQuality.no2, "µg/m³")
            AqiDetailRow("O₃", airQuality.o3, "µg/m³")
            AqiDetailRow("SO₂", airQuality.so2, "µg/m³")
            Spacer(modifier = Modifier.height(8.dp))
            AqiProgressBar(aqiIndex = airQuality.aqiIndex)
        }
    }
}

@Composable
private fun AqiDetailRow(
    label: String,
    value: Double,
    unit: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "%.1f $unit".format(value),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AqiProgressBar(aqiIndex: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = "500",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (aqiIndex / 500f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                aqiIndex <= 50 -> Color(0xFF4CAF50)
                aqiIndex <= 100 -> Color(0xFFFFEB3B)
                aqiIndex <= 150 -> Color(0xFFFF9800)
                aqiIndex <= 200 -> Color(0xFFFF5722)
                aqiIndex <= 300 -> Color(0xFF9C27B0)
                else -> Color(0xFF7B1FA2)
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun SunriseSunsetCard(
    forecastList: List<ForecastInfo>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Mặt trời mọc & lặn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            forecastList.take(3).forEach { forecast ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = forecast.date,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row {
                        Text(
                            text = "🌅 Mọc",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "🌇 Lặn",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}