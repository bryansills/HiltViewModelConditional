package com.example.hiltviewmodelconditional

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class CoolRoute(val message: String)

@Composable
fun CoolScreen(viewModel: CoolViewModel = hiltViewModel()) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "The cool screen message is: ${viewModel.route.message}")
        }
    }
}

@HiltViewModel
class CoolViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    val route = savedStateHandle.toRoute<CoolRoute>()
}