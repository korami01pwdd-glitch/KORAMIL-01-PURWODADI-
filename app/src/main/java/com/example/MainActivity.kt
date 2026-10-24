package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PortalBottomNav
import com.example.ui.components.PortalTopBar
import com.example.ui.screens.ActivityFormScreen
import com.example.ui.screens.ActivityListScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LessonsScreen
import com.example.ui.screens.SpreadsheetScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.authStateFlow
import com.example.viewmodel.KomsosViewModel
import com.example.viewmodel.KomsosViewModelFactory
import com.example.viewmodel.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = FirebaseAuth.getInstance()) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser
    if (user == null) {
        AuthScreen(auth = auth)
    } else {
        val context = LocalContext.current
        val databaseId = stringResource(R.string.firestore_database_id)
        val application = context.applicationContext as Application
        val viewModel: KomsosViewModel = viewModel(
            key = user.uid,
            factory = KomsosViewModelFactory(application, user.uid, databaseId)
        )
        MainApp(
            viewModel = viewModel,
            user = user,
            onSignOut = { auth.signOut() }
        )
    }
}

@Composable
fun MainApp(
    viewModel: KomsosViewModel,
    user: FirebaseUser,
    onSignOut: () -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val allActivities by viewModel.allActivities.collectAsState()
    val filteredActivities by viewModel.filteredActivities.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val selectedActivity by viewModel.selectedActivity.collectAsState()
    val quizState by viewModel.quizState.collectAsState()

    // Handle back button for sub-screens
    if (currentScreen != Screen.HOME) {
        BackHandler {
            if (currentScreen == Screen.ADD_ACTIVITY) {
                viewModel.selectActivity(null)
                viewModel.navigateTo(Screen.ACTIVITIES)
            } else {
                viewModel.navigateTo(Screen.HOME)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != Screen.ADD_ACTIVITY) {
                PortalTopBar(
                    userEmail = user.email ?: user.displayName ?: "Babinsa Terverifikasi",
                    onExportClick = if (currentScreen != Screen.SPREADSHEET) {
                        { viewModel.navigateTo(Screen.SPREADSHEET) }
                    } else null,
                    onSignOutClick = onSignOut
                )
            }
        },
        bottomBar = {
            if (currentScreen != Screen.ADD_ACTIVITY) {
                PortalBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        viewModel.selectActivity(null)
                        viewModel.navigateTo(screen)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        activities = allActivities,
                        onNavigate = { screen -> viewModel.navigateTo(screen) },
                        onSelectActivity = { act ->
                            viewModel.selectActivity(act)
                            viewModel.navigateTo(Screen.ACTIVITIES)
                        }
                    )
                }

                Screen.ACTIVITIES -> {
                    ActivityListScreen(
                        activities = filteredActivities,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.searchQuery.value = it },
                        categoryFilter = filterCategory,
                        onCategoryChange = { viewModel.filterCategory.value = it },
                        statusFilter = filterStatus,
                        onStatusChange = { viewModel.filterStatus.value = it },
                        onAddClick = {
                            viewModel.selectActivity(null)
                            viewModel.navigateTo(Screen.ADD_ACTIVITY)
                        },
                        onEditClick = { act ->
                            viewModel.selectActivity(act)
                            viewModel.navigateTo(Screen.ADD_ACTIVITY)
                        },
                        onDeleteClick = { act ->
                            viewModel.deleteActivity(act)
                        }
                    )
                }

                Screen.ADD_ACTIVITY -> {
                    ActivityFormScreen(
                        initialActivity = selectedActivity,
                        viewModel = viewModel,
                        onSave = { id, title, date, time, village, locationDetail, targetCategory,
                                   contactPerson, contactRole, topic, summary, followUpAction,
                                   status, photoUri, babinsaName, koramilName ->
                            viewModel.saveActivity(
                                id = id,
                                title = title,
                                date = date,
                                time = time,
                                village = village,
                                locationDetail = locationDetail,
                                targetCategory = targetCategory,
                                contactPerson = contactPerson,
                                contactRole = contactRole,
                                topic = topic,
                                summary = summary,
                                followUpAction = followUpAction,
                                status = status,
                                photoUri = photoUri,
                                babinsaName = babinsaName,
                                koramilName = koramilName,
                                onSuccess = {
                                    viewModel.navigateTo(Screen.ACTIVITIES)
                                }
                            )
                        },
                        onBack = {
                            viewModel.selectActivity(null)
                            viewModel.navigateTo(Screen.ACTIVITIES)
                        }
                    )
                }

                Screen.SPREADSHEET -> {
                    SpreadsheetScreen(
                        activities = allActivities,
                        onSelectActivity = { act ->
                            viewModel.selectActivity(act)
                            viewModel.navigateTo(Screen.ACTIVITIES)
                        }
                    )
                }

                Screen.LESSONS, Screen.QUIZ -> {
                    LessonsScreen(
                        modules = viewModel.modules,
                        quizQuestions = viewModel.quizQuestions,
                        quizState = quizState,
                        onSelectOption = { viewModel.selectQuizOption(it) },
                        onSubmitAnswer = { viewModel.submitQuizAnswer() },
                        onNextQuestion = { viewModel.nextQuizQuestion() },
                        onResetQuiz = { viewModel.resetQuiz() }
                    )
                }
            }
        }
    }
}
