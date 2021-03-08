/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = Color(251, 251, 251)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        var startCountDownTimer by remember { mutableStateOf(false) }
                        var countdownTimer by remember { mutableStateOf(100) }

                        if (startCountDownTimer) {
                            LaunchedEffect(key1 = false) {
                                while (isActive && countdownTimer > 0) {
                                    delay(1000)
                                    countdownTimer -= 1
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(300.dp)
                                .border(6.dp, color = Color.Red, CircleShape)
                                .clip(CircleShape)

                                .clickable {
                                    startCountDownTimer = startCountDownTimer.not()

                                    if (countdownTimer == 0) {
                                        countdownTimer = 100
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {

                            WaterWave(countdownTimer, startCountDownTimer)
                            Timer(countdownTimer, startCountDownTimer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Timer(countdownTimer: Int, timerStarted: Boolean) {
    val fontColor by animateColorAsState(
        targetValue = when (countdownTimer) {
            in 60..100 -> Color.White
            in 20..59 -> Color.Black
            else -> Color.Red
        }
    )

    Text(
        text = getStatus(countdownTimer, timerStarted),
        style = TextStyle(
            color = fontColor,
            fontWeight = FontWeight.ExtraBold, fontSize = 82.sp
        )
    )
}

private fun getStatus(countdownTimer: Int, timerStarted: Boolean): String {
    return when {
        countdownTimer == 0 || countdownTimer == 100 -> "Start"
        timerStarted.not() -> "Pause"
        else -> countdownTimer.toString()
    }
}

@Composable
fun WaterWave(countdownTimer: Int, timerStarted: Boolean) {

    val waveWidth = 250
    val path = androidx.compose.ui.graphics.Path()
    val originalY by animateIntAsState(
        targetValue =
        if (countdownTimer == 0) 300 else Math.abs(countdownTimer.times(3) - 300)
    )

    val waveHump by animateFloatAsState(targetValue = if (timerStarted) 40f else 0f)

    val deltaXAnim = rememberInfiniteTransition()
    val dx by deltaXAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing)
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        translate {
            drawPath(path = path, Color.Red)
            path.reset()

            val halfWaveWidth = waveWidth / 2
            path.moveTo(-waveWidth + (waveWidth * dx), originalY.dp.toPx())

            for (i in -waveWidth..(size.width.toInt() + waveWidth) step waveWidth) {
                path.relativeQuadraticBezierTo(
                    20f,
                    waveHump,
                    halfWaveWidth.toFloat(),
                    0f
                )
                path.relativeQuadraticBezierTo(
                    halfWaveWidth.toFloat() / 2,
                    waveHump,
                    halfWaveWidth.toFloat(),
                    0f
                )
            }

            path.lineTo(size.width, size.height)
            path.lineTo(0f, size.height)
            path.close()
        }
    }
}
