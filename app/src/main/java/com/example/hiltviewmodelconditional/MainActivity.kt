package com.example.hiltviewmodelconditional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hiltviewmodelconditional.ui.theme.HiltViewModelConditionalTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiltViewModelConditionalTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = HomeRoute) {
                    composable<HomeRoute> {
                        HomeScreen(
                            goToCoolScreen = {
                                navController.navigate(CoolRoute("howdy"))
                            }
                        )
                    }
                    composable<CoolRoute> { CoolScreen() }
                }
            }
        }
    }
}

@Serializable
data object HomeRoute

@Composable
fun HomeScreen(goToCoolScreen: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "Home screen")
            Button(onClick = goToCoolScreen) { Text(text = "Wanna see something cool?") }
        }
    }
}