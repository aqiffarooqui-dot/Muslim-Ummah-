package com.example.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AuthDialog
import com.example.ui.components.PaywallModal
import com.example.ui.screens.*
import com.example.ui.viewmodel.MuslimViewModel

sealed class Screen(val route: String, val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "Prayers", Icons.Filled.AccessTimeFilled, Icons.Outlined.AccessTime)
    object Quran : Screen("quran", "Quran", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
    object Qibla : Screen("qibla", "Qibla", Icons.Filled.Explore, Icons.Outlined.Explore)
    object Tasbih : Screen("tasbih", "Tasbih", Icons.Filled.RadioButtonChecked, Icons.Outlined.RadioButtonUnchecked)
    object More : Screen("more", "More", Icons.Filled.Widgets, Icons.Outlined.Widgets)
    // Sub-screens
    object Duas : Screen("duas", "Duas", Icons.Filled.VolunteerActivism, Icons.Outlined.VolunteerActivism)
    object NamesOfAllah : Screen("names", "99 Names", Icons.Filled.Star, Icons.Outlined.StarOutline)
    object Calendar : Screen("calendar", "Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    object Admin : Screen("admin", "Admin", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)
    object FastingQada : Screen("fasting_qada", "Ramadan", Icons.Filled.Restaurant, Icons.Outlined.Restaurant)
}

@Composable
fun MainAppScreen(
    viewModel: MuslimViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val tasbihHistory by viewModel.tasbihHistory.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showAuthDialog by remember { mutableStateOf(false) }

    // System Back Handler
    BackHandler(enabled = currentScreen != Screen.Home) {
        currentScreen = when (currentScreen) {
            Screen.Duas, Screen.NamesOfAllah, Screen.Calendar, Screen.Settings, Screen.Admin, Screen.FastingQada -> Screen.More
            else -> Screen.Home
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            val primaryScreens = listOf(Screen.Home, Screen.Quran, Screen.Qibla, Screen.Tasbih, Screen.More)
            NavigationBar(
                modifier = Modifier
                    .testTag("bottom_nav_bar")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                tonalElevation = 8.dp
            ) {
                primaryScreens.forEach { screen ->
                    val isSelected = when (screen) {
                        Screen.More -> currentScreen in listOf(
                            Screen.More,
                            Screen.Duas,
                            Screen.NamesOfAllah,
                            Screen.Calendar,
                            Screen.Settings,
                            Screen.Admin,
                            Screen.FastingQada
                        )
                        else -> currentScreen == screen
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(text = screen.label) },
                        modifier = Modifier.testTag("nav_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            label = "screen_transition",
            modifier = Modifier.padding(innerPadding)
        ) { screen ->
            when (screen) {
                Screen.Home -> HomeScreen(
                    uiState = uiState,
                    onNavigate = { route ->
                        currentScreen = when (route) {
                            "qibla" -> Screen.Qibla
                            "quran" -> Screen.Quran
                            "tasbih" -> Screen.Tasbih
                            "duas" -> Screen.Duas
                            "names" -> Screen.NamesOfAllah
                            "calendar" -> Screen.Calendar
                            "settings" -> Screen.Settings
                            "admin" -> Screen.Admin
                            "fasting_qada" -> Screen.FastingQada
                            else -> Screen.Home
                        }
                    },
                    onTogglePrayer = { viewModel.togglePrayer(it) },
                    onSelectCity = { viewModel.setCity(it) },
                    onOpenPaywall = { viewModel.showPaywall("Muslim Pro Premium") },
                    onOpenAuth = { showAuthDialog = true }
                )
                Screen.Quran -> QuranScreen(
                    uiState = uiState,
                    audioState = audioState,
                    onSearchChange = { viewModel.setQuranSearch(it) },
                    onPlaySurah = { viewModel.playSurahAudio(it) },
                    onPauseAudio = { viewModel.audioPlayer.pause() },
                    onResumeAudio = { viewModel.audioPlayer.resume() },
                    onToggleBookmark = { type, ref, sec, title, sub ->
                        viewModel.toggleBookmark(type, ref, sec, title, sub)
                    },
                    isBookmarked = { type, ref, sec ->
                        viewModel.isItemBookmarked(type, ref, sec)
                    }
                )
                Screen.Qibla -> QiblaScreen(uiState = uiState)
                Screen.Tasbih -> TasbihScreen(
                    uiState = uiState,
                    tasbihHistory = tasbihHistory,
                    onIncrement = { viewModel.incrementTasbih() },
                    onReset = { viewModel.resetTasbih() },
                    onSelectDhikr = { viewModel.selectDhikr(it) },
                    onSetTarget = { viewModel.setTasbihTarget(it) },
                    onToggleVibration = { viewModel.toggleTasbihVibration() }
                )
                Screen.More -> MoreHubScreen(
                    uiState = uiState,
                    onNavigate = { route ->
                        currentScreen = when (route) {
                            "duas" -> Screen.Duas
                            "names" -> Screen.NamesOfAllah
                            "calendar" -> Screen.Calendar
                            "settings" -> Screen.Settings
                            "admin" -> Screen.Admin
                            "fasting_qada" -> Screen.FastingQada
                            else -> Screen.Home
                        }
                    }
                )
                Screen.Duas -> DuasScreen(
                    uiState = uiState,
                    onCategorySelect = { viewModel.setDuaCategory(it) },
                    onSearchChange = { viewModel.setDuaSearch(it) },
                    onToggleBookmark = { type, ref, sec, title, sub ->
                        viewModel.toggleBookmark(type, ref, sec, title, sub)
                    },
                    isBookmarked = { type, ref, sec ->
                        viewModel.isItemBookmarked(type, ref, sec)
                    }
                )
                Screen.NamesOfAllah -> NamesOfAllahScreen(uiState = uiState)
                Screen.Calendar -> CalendarScreen(uiState = uiState)
                Screen.FastingQada -> FastingQadaScreen(
                    uiState = uiState,
                    onIncrementQada = { viewModel.incrementQada(it) },
                    onDecrementQada = { viewModel.decrementQada(it) },
                    onShowPaywall = { viewModel.showPaywall(it) }
                )
                Screen.Admin -> AdminScreen(
                    uiState = uiState,
                    allUsers = allUsers,
                    onUpdateSubscription = { email, isPrem, plan, duration ->
                        viewModel.updateUserSubscription(email, isPrem, plan, duration)
                    },
                    onDeleteUser = { email -> viewModel.deleteUserByAdmin(email) },
                    onAddNewUser = { email, name, isPrem, plan ->
                        viewModel.addNewUserByAdmin(email, name, isPrem, plan)
                    },
                    onSearchChange = { viewModel.setAdminUserSearch(it) },
                    onFilterPlanChange = { viewModel.setAdminFilterPlan(it) }
                )
                Screen.Settings -> SettingsScreen(
                    uiState = uiState,
                    onSelectCity = { viewModel.setCity(it) },
                    onSetMethod = { viewModel.setCalculationMethod(it) },
                    onSetJuristic = { viewModel.setJuristicMethod(it) },
                    onOpenAuth = { showAuthDialog = true },
                    onOpenAdmin = { currentScreen = Screen.Admin },
                    onOpenPaywall = { viewModel.showPaywall("Muslim Pro Premium") },
                    onSelectReciter = { viewModel.setReciter(it) },
                    onSelectAdhan = { viewModel.setAdhanSound(it) }
                )
            }
        }
    }

    // Paywall Dialog
    if (uiState.showPaywallModal) {
        PaywallModal(
            featureTrigger = uiState.paywallTriggerFeature,
            onDismiss = { viewModel.dismissPaywall() },
            onSubscribe = { plan -> viewModel.subscribePlan(plan) },
            onLoginAsAdmin = {
                viewModel.loginAsAdmin()
                viewModel.dismissPaywall()
            }
        )
    }

    // Auth & Google Sign-In Dialog
    if (showAuthDialog) {
        AuthDialog(
            uiState = uiState,
            allUsers = allUsers,
            onDismiss = { showAuthDialog = false },
            onSignInWithGoogle = { activity ->
                viewModel.signInWithGoogle(activity)
                showAuthDialog = false
            },
            onSwitchAccount = { email ->
                viewModel.switchAccount(email)
                showAuthDialog = false
            },
            onSignOut = {
                viewModel.signOut()
                showAuthDialog = false
            },
            onLoginAsAdmin = {
                viewModel.loginAsAdmin()
                showAuthDialog = false
            }
        )
    }
}
