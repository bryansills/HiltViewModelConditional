package com.example.hiltviewmodelconditional

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navOptions
import androidx.navigation.navigation
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

enum class ApprovalStatus { Waiting, Approved, Denied }

@HiltViewModel
class ApprovalViewModel @Inject constructor() : ViewModel() {
    val _status = MutableStateFlow(ApprovalStatus.Waiting)
    val status = _status.asStateFlow()

    init {
        Log.d("BLARG", "creating new approval view model")
    }

    fun approve() {
        _status.value = ApprovalStatus.Approved
    }

    fun deny() {
        _status.value = ApprovalStatus.Denied
    }
}

@Serializable
data object HazardRoute

@Composable
fun HazardScreen(onApprove: () -> Unit, onDeny: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "Are you sure?")
            Button(onClick = onApprove) { Text("Let's do it") }
            Button(onClick = onDeny) { Text("ehhhhhh") }
        }
    }
}

inline fun <reified ParentRoute : Any, reified ApprovedRoute : Any> NavGraphBuilder.approvableDestination(
    navController: NavController,
    restoredRoute: ApprovedRoute,
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    navigation<ParentRoute>(startDestination = ApprovedRoute::class) {
        dialog<HazardRoute> { backstackEntry ->
            val parentEntry = remember(backstackEntry) {
                navController.getBackStackEntry<ParentRoute>()
            }
            val parentViewModel = hiltViewModel<ApprovalViewModel>(parentEntry)
            HazardScreen(
                onApprove = {
                    parentViewModel.approve()
                    navController.navigate(
                        route = restoredRoute,
                        navOptions = navOptions {
                            restoreState = true
                            popUpTo<HazardRoute> { inclusive = true }
                        }
                    )
                },
                onDeny = {
                    parentViewModel.deny()
                    navController.popBackStack()
                }
            )
        }
        composable<ApprovedRoute> { backstackEntry ->
            val parentEntry = remember(backstackEntry) {
                navController.getBackStackEntry<ParentRoute>()
            }
            val parentViewModel = hiltViewModel<ApprovalViewModel>(parentEntry)
            val approvalStatus by parentViewModel.status.collectAsState()

            when (approvalStatus) {
                ApprovalStatus.Waiting -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(
                            route = HazardRoute,
                            navOptions = navOptions {
                                popUpTo<ParentRoute> {
                                    saveState = true
                                }
                            }
                        )
                    }
                }
                ApprovalStatus.Denied -> {
                    LaunchedEffect(Unit) {
                        navController.popBackStack<ParentRoute>(inclusive = true)
                    }
                }
                ApprovalStatus.Approved -> {
                    content(backstackEntry)
                }
            }
        }
    }
}

@Serializable
data object ParentCoolRoute