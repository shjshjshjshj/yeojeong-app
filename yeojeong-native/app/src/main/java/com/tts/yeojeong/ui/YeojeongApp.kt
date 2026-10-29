package com.tts.yeojeong.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tts.yeojeong.data.SampleSpots
import com.tts.yeojeong.ui.components.YBottomBar
import com.tts.yeojeong.ui.components.YLogo
import com.tts.yeojeong.ui.screens.HomeScreen
import com.tts.yeojeong.ui.screens.LoginScreen
import com.tts.yeojeong.ui.screens.OnboardingScreen
import com.tts.yeojeong.ui.screens.ProfileSetupScreen
import com.tts.yeojeong.ui.screens.DetailSetupScreen
import com.tts.yeojeong.ui.screens.ReportScreen
import com.tts.yeojeong.ui.screens.RouteScreen
import com.tts.yeojeong.ui.screens.SavedScreen
import com.tts.yeojeong.ui.screens.SearchScreen
import com.tts.yeojeong.ui.screens.SettingsScreen
import com.tts.yeojeong.ui.screens.SpotDetailScreen
import com.tts.yeojeong.ui.screens.TermsScreen
import com.tts.yeojeong.ui.screens.WriteReviewScreen
import com.tts.yeojeong.ui.theme.Primary
import com.tts.yeojeong.ui.theme.SurfaceWhite
import com.tts.yeojeong.ui.theme.SystemBarIcons
import com.tts.yeojeong.ui.theme.TextSecondary
import com.tts.yeojeong.vm.YeojeongViewModel

private object Dest {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val DETAIL_SETUP = "detail_setup"
    const val HOME = "home"
    const val ROUTE = "route"
    const val REPORT = "report"
    const val SAVED = "saved"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val TERMS = "terms"
    const val SPOT = "spot/{spotId}"
    const val WRITE_REVIEW = "review/{spotId}"
}

@Composable
fun YeojeongApp(vm: YeojeongViewModel = viewModel()) {
    val prefs by vm.prefs.collectAsStateWithLifecycle()
    val weather by vm.weather.collectAsStateWithLifecycle()
    val route by vm.route.collectAsStateWithLifecycle()
    val subway by vm.subway.collectAsStateWithLifecycle()
    val homeSpots by vm.homeSpots.collectAsStateWithLifecycle()
    val routeSpots by vm.routeSpots.collectAsStateWithLifecycle()
    val searchResults by vm.searchResults.collectAsStateWithLifecycle()
    val searching by vm.searching.collectAsStateWithLifecycle()
    val knownSpots by vm.knownSpots.collectAsStateWithLifecycle()

    if (!prefs.loaded) {
        SplashScreen()
        return
    }

    val nav = rememberNavController()
    val startDestination = remember {
        if (prefs.onboardingDone) Dest.HOME else Dest.ONBOARDING
    }
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute in setOf(Dest.HOME, Dest.ROUTE, Dest.REPORT, Dest.SAVED)
    val selectedTab = when (currentRoute) {
        Dest.ROUTE -> "route"
        Dest.REPORT -> "report"
        Dest.SAVED -> "my"
        else -> "home"
    }
    val context = LocalContext.current
    val setupNickname = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(prefs.nickname)
    }
    SystemBarIcons(
        useDarkIcons = currentRoute != Dest.ROUTE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SurfaceWhite,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomBar) {
                YBottomBar(
                    selected = selectedTab,
                    onSelect = { key ->
                        val target = when (key) {
                            "route" -> Dest.ROUTE
                            "report" -> Dest.REPORT
                            "my" -> Dest.SAVED
                            else -> Dest.HOME
                        }
                        nav.navigate(target) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Dest.ONBOARDING) {
                OnboardingScreen(
                    onFinish = { nav.navigate(Dest.LOGIN) }
                )
            }
            composable(Dest.LOGIN) {
                LoginScreen(onContinue = { nav.navigate(Dest.PROFILE) })
            }
            composable(Dest.PROFILE) {
                ProfileSetupScreen(
                    onBack = { nav.popBackStack() },
                    onNext = { name ->
                        setupNickname.value = name
                        nav.navigate(Dest.DETAIL_SETUP)
                    }
                )
            }
            composable(Dest.DETAIL_SETUP) {
                DetailSetupScreen(
                    nickname = setupNickname.value,
                    onBack = { nav.popBackStack() },
                    onFinish = { gender, mbti ->
                        vm.updateProfile(setupNickname.value, gender, mbti)
                        vm.completeOnboarding()
                        nav.navigate(Dest.HOME) {
                            popUpTo(Dest.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
            composable(Dest.HOME) {
                HomeScreen(
                    prefs = prefs,
                    weather = weather,
                    route = route,
                    spots = homeSpots,
                    onSpotClick = { nav.navigate("spot/${it.id}") },
                    onSearch = { nav.navigate(Dest.SEARCH) },
                    onRouteClick = { nav.navigate(Dest.ROUTE) },
                    onRefreshWeather = vm::refreshWeather
                )
            }
            composable(Dest.ROUTE) {
                RouteScreen(
                    prefs = prefs,
                    route = route,
                    subway = subway,
                    availableSpots = routeSpots,
                    onSpotClick = { nav.navigate("spot/${it.id}") },
                    onUpdateRoute = vm::updateRoute,
                    onSettings = { nav.navigate(Dest.SETTINGS) }
                )
            }
            composable(Dest.REPORT) {
                ReportScreen(
                    prefs = prefs,
                    spots = knownSpots,
                    onSpotClick = { nav.navigate("spot/${it.id}") },
                    onExplore = { nav.navigate(Dest.HOME) },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Dest.SAVED) {
                SavedScreen(
                    prefs = prefs,
                    spots = knownSpots,
                    onSpotClick = { nav.navigate("spot/${it.id}") },
                    onSettings = { nav.navigate(Dest.SETTINGS) },
                    onExplore = { nav.navigate(Dest.HOME) },
                    onReport = { nav.navigate(Dest.REPORT) },
                    onDeleteReview = vm::deleteReview
                )
            }
            composable(Dest.SEARCH) {
                SearchScreen(
                    prefs = prefs,
                    results = searchResults,
                    searching = searching,
                    popularSpots = homeSpots,
                    onSearch = vm::search,
                    onSpotClick = { nav.navigate("spot/${it.id}") },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Dest.SETTINGS) {
                SettingsScreen(
                    prefs = prefs,
                    onBack = { nav.popBackStack() },
                    onNickname = vm::updateNickname,
                    onNotifications = vm::setNotifications,
                    onEditRoute = { nav.navigate(Dest.ROUTE) },
                    onTerms = { nav.navigate(Dest.TERMS) },
                    onReset = {
                        vm.resetData()
                        nav.navigate(Dest.ONBOARDING) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Dest.TERMS) {
                TermsScreen(onBack = { nav.popBackStack() })
            }
            composable(
                route = Dest.SPOT,
                arguments = listOf(navArgument("spotId") { type = NavType.StringType })
            ) { entry ->
                val spotId = entry.arguments?.getString("spotId").orEmpty()
                val spot = knownSpots.firstOrNull { it.id == spotId }
                if (spot == null) {
                    nav.popBackStack()
                } else {
                    LaunchedEffect(spot.id, spot.overview) {
                        vm.loadDetail(spot)
                    }
                    SpotDetailScreen(
                        spot = spot,
                        prefs = prefs,
                        onBack = { nav.popBackStack() },
                        onToggleSaved = { vm.toggleSaved(spot) },
                        onVisited = { vm.markVisited(spot) },
                        onWriteReview = { nav.navigate("review/${spot.id}") },
                        onShare = {
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "${spot.name} · ${spot.station}역 도보 ${spot.walkMinutes}분\n${spot.tagline}"
                                )
                            }
                            context.startActivity(Intent.createChooser(share, "여정 공유"))
                        },
                        onMap = {
                            val uri = Uri.parse(
                                "https://www.google.com/maps/search/?api=1&query=${spot.lat},${spot.lng}"
                            )
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        }
                    )
                }
            }
            composable(
                route = Dest.WRITE_REVIEW,
                arguments = listOf(navArgument("spotId") { type = NavType.StringType })
            ) { entry ->
                val spotId = entry.arguments?.getString("spotId").orEmpty()
                val spot = knownSpots.firstOrNull { it.id == spotId }
                if (spot == null) {
                    nav.popBackStack()
                } else {
                    WriteReviewScreen(
                        spot = spot,
                        onBack = { nav.popBackStack() },
                        onSave = { title, content, keywords, photos ->
                            vm.addReview(spot, title, content, keywords, photos)
                            nav.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    SystemBarIcons(useDarkIcons = true)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWhite)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                YLogo(modifier = Modifier.size(140.dp))
                Spacer(Modifier.size(20.dp))
                Column {
                    Text(
                        text = "여정",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                    Text(
                        text = "YeoJeong",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
