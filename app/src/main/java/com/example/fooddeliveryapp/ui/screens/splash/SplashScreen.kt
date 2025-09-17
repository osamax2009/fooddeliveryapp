package com.example.fooddeliveryapp.ui.screens.splash

import android.R.attr.contentDescription
import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.*
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fooddeliveryapp.data.SessionManager
import kotlinx.coroutines.delay
import com.example.foodhub.R
import javax.inject.Inject


@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    isLoggedIn: Boolean,
) {
    LaunchedEffect(Unit) {
        delay(4000) // Show splash for 2 seconds
        if (isLoggedIn) {
            onNavigateToHome()
        } else {
            onNavigateToAuth()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF6B47)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier.size(120.dp)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardColors().let {
                    CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        modifier = Modifier.size(80.dp),
                        contentDescription = "logo"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Food Hub",
                color =Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(120.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(80.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color.White
            )

        }
    }


}
