package com.tts.yeojeong.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tts.yeojeong.data.AppPreferences
import com.tts.yeojeong.data.SampleSpots
import com.tts.yeojeong.data.SubwayRepository
import com.tts.yeojeong.data.TourApiRepository
import com.tts.yeojeong.data.WeatherRepository
import com.tts.yeojeong.data.ReminderScheduler
import com.tts.yeojeong.model.Prefs
import com.tts.yeojeong.model.RoutePlan
import com.tts.yeojeong.model.Review
import com.tts.yeojeong.model.Spot
import com.tts.yeojeong.model.WeatherUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID

class YeojeongViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = AppPreferences(application)
    private val weatherRepository = WeatherRepository()
    private val tourApi = TourApiRepository(application)
    private var searchJob: Job? = null
    private val detailLoading = mutableSetOf<String>()

    val prefs: StateFlow<Prefs> = preferences.flow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Prefs()
    )

    val subway: StateFlow<SubwayRepository?> = flow {
        emit(withContext(Dispatchers.IO) { SubwayRepository.load(getApplication()) })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val route: StateFlow<RoutePlan?> = combine(prefs, subway) { currentPrefs, graph ->
        graph?.findRoute(currentPrefs.homeStation, currentPrefs.workStation)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    private val _homeSpots = MutableStateFlow(SampleSpots.all)
    val homeSpots: StateFlow<List<Spot>> = _homeSpots.asStateFlow()

    private val _routeSpots = MutableStateFlow(SampleSpots.all)
    val routeSpots: StateFlow<List<Spot>> = _routeSpots.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Spot>>(emptyList())
    val searchResults: StateFlow<List<Spot>> = _searchResults.asStateFlow()

    private val _searching = MutableStateFlow(false)
    val searching: StateFlow<Boolean> = _searching.asStateFlow()

    private val _knownSpots = MutableStateFlow(
        SampleSpots.all.associateBy { it.id }
    )
    val knownSpots: StateFlow<List<Spot>> = _knownSpots
        .map { it.values.toList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SampleSpots.all
        )

    private val _weather = MutableStateFlow<WeatherUi>(WeatherUi.Loading)
    val weather: StateFlow<WeatherUi> = _weather.asStateFlow()

    init {
        refreshWeather()
        viewModelScope.launch {
            preferences.flow
                .map { it.notifications }
                .distinctUntilChanged()
                .collect { enabled ->
                    if (enabled) ReminderScheduler.schedule(getApplication())
                    else ReminderScheduler.cancel(getApplication())
                }
        }
        viewModelScope.launch {
            combine(prefs, subway) { currentPrefs, graph ->
                currentPrefs.homeStation to graph
            }
                .distinctUntilChanged()
                .collectLatest { (homeStation, graph) ->
                    if (graph == null) return@collectLatest
                    val origin = graph.station(homeStation)
                    val seedHome = tourApi.seedNear(
                        lat = origin?.lat ?: 37.5665,
                        lng = origin?.lng ?: 126.9780,
                        stations = graph.stations,
                        radius = 2000
                    )
                    if (seedHome.isNotEmpty()) {
                        remember(seedHome)
                        _homeSpots.value = seedHome
                    }
                    val home = tourApi.nearby(
                        lat = origin?.lat ?: 37.5665,
                        lng = origin?.lng ?: 126.9780,
                        stations = graph.stations,
                        radius = 2000,
                        rows = 50
                    ).ifEmpty { seedHome.ifEmpty { SampleSpots.all } }
                    remember(home)
                    _homeSpots.value = home
                }
        }

        viewModelScope.launch {
            combine(subway, route) { graph, currentRoute ->
                graph to currentRoute
            }
                .distinctUntilChanged()
                .collectLatest { (graph, currentRoute) ->
                    if (graph == null || currentRoute == null) return@collectLatest
                    // 노선 위 각 역의 좌표를 기준으로 도보권 장소를 찾습니다.
                    // 경로가 길면 호출 수를 줄이기 위해 역을 균등하게 샘플링합니다.
                    val path = currentRoute.stations
                    val step = (path.size / 8).coerceAtLeast(1)
                    val targets = path.filterIndexed { index, _ ->
                        index == 0 || index == path.lastIndex || index % step == 0
                    }.distinct().take(9)
                    val collected = LinkedHashMap<String, MutableList<Spot>>()
                    for (stationName in targets) {
                        val station = graph.station(stationName) ?: continue
                        val remote = tourApi.nearby(
                            lat = station.lat,
                            lng = station.lng,
                            stations = graph.stations,
                            radius = 900,
                            rows = 12
                        ).filter { it.station == stationName }
                        val local = tourApi.seedNear(
                            lat = station.lat,
                            lng = station.lng,
                            stations = graph.stations,
                            radius = 900
                        ).filter { it.station == stationName }
                        val merged = (remote + local.filter { spot ->
                            remote.none { it.contentId == spot.contentId }
                        })
                            .distinctBy { it.contentId ?: it.id }
                            .take(6)
                        if (merged.isNotEmpty()) collected[stationName] = merged.toMutableList()
                    }
                    val result = if (collected.isEmpty()) {
                        SampleSpots.all.filter { it.station in path }
                    } else collected.values.flatten()
                    remember(result)
                    _routeSpots.value = result
                }
        }
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _weather.value = WeatherUi.Loading
            _weather.value = weatherRepository.current()
        }
    }

    fun search(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _searching.value = false
            return
        }
        searchJob = viewModelScope.launch {
            _searching.value = true
            val graph = subway.value
            val remote = tourApi.search(
                keyword = query,
                stations = graph?.stations.orEmpty()
            )
            val local = SampleSpots.all.filter { spot ->
                spot.name.contains(query, ignoreCase = true) ||
                    spot.station.contains(query, ignoreCase = true) ||
                    spot.category.contains(query, ignoreCase = true) ||
                    spot.tags.any { it.contains(query, ignoreCase = true) }
            }
            val merged = (remote + local)
                .distinctBy { it.contentId ?: it.id }
            remember(merged)
            _searchResults.value = merged
            _searching.value = false
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch { preferences.setOnboardingDone() }
    }

    fun updateRoute(home: String, work: String) {
        viewModelScope.launch { preferences.setRoute(home, work) }
    }

    fun updateNickname(name: String) {
        viewModelScope.launch { preferences.setNickname(name) }
    }

    fun updateProfile(name: String, gender: String, mbti: String) {
        viewModelScope.launch { preferences.setProfile(name, gender, mbti) }
    }

    fun toggleSaved(spot: Spot) {
        viewModelScope.launch {
            preferences.toggleSaved(spot.id)
        }
    }

    fun markVisited(spot: Spot) {
        viewModelScope.launch {
            preferences.markVisited(spot.id)
        }
    }

    fun addReview(
        spot: Spot,
        title: String,
        content: String,
        keywords: List<String>,
        photos: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            val review = Review(
                id = UUID.randomUUID().toString(),
                spotId = spot.id,
                spotName = spot.name,
                nickname = prefs.value.nickname,
                title = title,
                content = content,
                keywords = keywords,
                epochDay = LocalDate.now().toEpochDay(),
                photos = photos
            )
            preferences.addReview(review)
        }
    }

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            preferences.deleteReview(reviewId)
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch { preferences.setNotifications(enabled) }
    }

    fun resetData() {
        viewModelScope.launch { preferences.reset() }
    }

    fun rememberSpot(spot: Spot) {
        remember(listOf(spot))
    }

    fun loadDetail(spot: Spot) {
        val contentId = spot.contentId ?: return
        if (spot.overview.isNotBlank() || contentId in detailLoading) return
        detailLoading.add(contentId)
        viewModelScope.launch {
            val enriched = tourApi.detail(spot)
            remember(listOf(enriched))
            detailLoading.remove(contentId)
        }
    }

    private fun remember(spots: List<Spot>) {
        if (spots.isEmpty()) return
        _knownSpots.update { current ->
            current + spots.associateBy { it.id }
        }
    }
}
