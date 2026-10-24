package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.KomsosDatabase
import com.example.data.model.KomsosActivity
import com.example.data.model.LessonModule
import com.example.data.model.QuizQuestion
import com.example.data.repository.FirestoreKomsosRepository
import com.example.data.repository.KomsosRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    ACTIVITIES,
    ADD_ACTIVITY,
    SPREADSHEET,
    LESSONS,
    QUIZ
}

data class QuizState(
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isSubmitted: Boolean = false,
    val score: Int = 0,
    val isCompleted: Boolean = false
)

class KomsosViewModel(
    application: Application,
    val currentUserId: String,
    val databaseId: String
) : AndroidViewModel(application) {

    private val repository: KomsosRepository
    private val firestoreRepository: FirestoreKomsosRepository

    init {
        val db = KomsosDatabase.getDatabase(application)
        repository = KomsosRepository(db.komsosDao())
        val firestore = FirebaseFirestore.getInstance(FirebaseApp.getInstance(), databaseId)
        val auth = FirebaseAuth.getInstance()
        firestoreRepository = FirestoreKomsosRepository(firestore, auth)

        viewModelScope.launch {
            repository.seedIfEmpty()
            try {
                firestoreRepository.observeActivities().collect { cloudActivities ->
                    for (cloudAct in cloudActivities) {
                        repository.insertOrUpdateByFirestore(cloudAct)
                    }
                }
            } catch (e: Exception) {
                Log.e("KomsosViewModel", "Error syncing with Firestore", e)
            }
        }
    }

    // Navigation state
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Raw activities from database
    val allActivities: StateFlow<List<KomsosActivity>> = repository.allActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filter states
    val searchQuery = MutableStateFlow("")
    val filterCategory = MutableStateFlow("Semua")
    val filterStatus = MutableStateFlow("Semua")

    // Filtered activities stream
    val filteredActivities: StateFlow<List<KomsosActivity>> = combine(
        allActivities,
        searchQuery,
        filterCategory,
        filterStatus
    ) { list, query, category, status ->
        list.filter { activity ->
            val matchesQuery = query.isBlank() ||
                activity.title.contains(query, ignoreCase = true) ||
                activity.contactPerson.contains(query, ignoreCase = true) ||
                activity.village.contains(query, ignoreCase = true) ||
                activity.topic.contains(query, ignoreCase = true) ||
                activity.summary.contains(query, ignoreCase = true)

            val matchesCategory = category == "Semua" || activity.targetCategory == category
            val matchesStatus = status == "Semua" || activity.status == status

            matchesQuery && matchesCategory && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected activity for detail or edit
    private val _selectedActivity = MutableStateFlow<KomsosActivity?>(null)
    val selectedActivity: StateFlow<KomsosActivity?> = _selectedActivity.asStateFlow()

    fun selectActivity(activity: KomsosActivity?) {
        _selectedActivity.value = activity
    }

    val firestoreRepo: FirestoreKomsosRepository
        get() = firestoreRepository

    // CRUD Operations with Room + Firestore dual sync
    fun saveActivity(
        id: Long = 0,
        title: String,
        date: String,
        time: String,
        village: String,
        locationDetail: String,
        targetCategory: String,
        contactPerson: String,
        contactRole: String,
        topic: String,
        summary: String,
        followUpAction: String,
        status: String,
        photoUri: String? = null,
        babinsaName: String,
        koramilName: String,
        participantCount: Int = 1,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val existing = if (id != 0L) repository.getActivityById(id) else null
            val firestoreId = existing?.firestoreId ?: ""

            val activity = KomsosActivity(
                id = id,
                firestoreId = firestoreId,
                title = title.trim(),
                date = date.trim(),
                time = time.trim(),
                village = village.trim(),
                locationDetail = locationDetail.trim(),
                targetCategory = targetCategory.trim(),
                contactPerson = contactPerson.trim(),
                contactRole = contactRole.trim(),
                topic = topic.trim(),
                summary = summary.trim(),
                followUpAction = followUpAction.trim(),
                status = status,
                photoUri = photoUri,
                babinsaName = babinsaName.ifBlank { "Serka Bambang Suryono" },
                koramilName = koramilName.ifBlank { "Koramil 08/Binaan" },
                participantCount = participantCount
            )

            if (id == 0L) {
                val localId = repository.insertActivity(activity)
                try {
                    val result = firestoreRepository.createActivity(activity.copy(id = localId))
                    val newFirestoreId = result.getOrNull()
                    if (!newFirestoreId.isNullOrBlank()) {
                        repository.updateActivity(activity.copy(id = localId, firestoreId = newFirestoreId))
                    }
                } catch (e: Exception) {
                    Log.e("KomsosViewModel", "Error uploading to Firestore", e)
                }
            } else {
                repository.updateActivity(activity)
                try {
                    if (firestoreId.isNotBlank()) {
                        firestoreRepository.updateActivity(firestoreId, activity)
                    } else {
                        val result = firestoreRepository.createActivity(activity)
                        val newFirestoreId = result.getOrNull()
                        if (!newFirestoreId.isNullOrBlank()) {
                            repository.updateActivity(activity.copy(firestoreId = newFirestoreId))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("KomsosViewModel", "Error updating in Firestore", e)
                }
            }
            _selectedActivity.value = null
            onSuccess()
        }
    }

    // Direct Firestore save specifically tailored for ActivityForm (Date, Location, Description, Participant Count)
    fun saveKomsosActivityToFirestore(
        date: String,
        location: String,
        description: String,
        participantCount: Int,
        title: String = "",
        onSuccess: (String) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val autoTitle = if (title.isNotBlank()) title.trim() else "Komsos di ${location.trim()}"
                val nowTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                val activity = KomsosActivity(
                    title = autoTitle,
                    date = date.trim(),
                    time = nowTime,
                    village = location.trim(),
                    locationDetail = location.trim(),
                    targetCategory = "Warga & Tokoh Masyarakat",
                    contactPerson = "Warga $location",
                    contactRole = "Peserta Komsos",
                    topic = description.take(60).trim(),
                    summary = description.trim(),
                    followUpAction = "Pemantauan rutin wilayah binaan",
                    status = "Selesai",
                    participantCount = participantCount,
                    babinsaName = "Serka Bambang Suryono",
                    koramilName = "Koramil 08/Binaan"
                )

                val localId = repository.insertActivity(activity)
                val result = firestoreRepository.createActivity(activity.copy(id = localId))
                if (result.isSuccess) {
                    val firestoreId = result.getOrNull() ?: ""
                    if (firestoreId.isNotBlank()) {
                        repository.updateActivity(activity.copy(id = localId, firestoreId = firestoreId))
                    }
                    _selectedActivity.value = null
                    onSuccess(firestoreId)
                } else {
                    val err = result.exceptionOrNull() ?: Exception("Gagal menyimpan ke Firestore")
                    onError(err)
                }
            } catch (e: Exception) {
                Log.e("KomsosViewModel", "Error in saveKomsosActivityToFirestore", e)
                onError(e)
            }
        }
    }

    fun deleteActivity(activity: KomsosActivity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
            if (activity.firestoreId.isNotBlank()) {
                try {
                    firestoreRepository.deleteActivity(activity.firestoreId)
                } catch (e: Exception) {
                    Log.e("KomsosViewModel", "Error deleting from Firestore", e)
                }
            }
            if (_selectedActivity.value?.id == activity.id) {
                _selectedActivity.value = null
            }
        }
    }

    // Lessons and education
    val modules: List<LessonModule> = repository.getModules()
    private val _selectedModule = MutableStateFlow<LessonModule?>(null)
    val selectedModule: StateFlow<LessonModule?> = _selectedModule.asStateFlow()

    fun selectModule(module: LessonModule?) {
        _selectedModule.value = module
    }

    // Quiz logic
    val quizQuestions: List<QuizQuestion> = repository.getQuizQuestions()
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    fun selectQuizOption(index: Int) {
        if (!_quizState.value.isSubmitted) {
            _quizState.value = _quizState.value.copy(selectedOptionIndex = index)
        }
    }

    fun submitQuizAnswer() {
        val current = _quizState.value
        val question = quizQuestions[current.currentQuestionIndex]
        val isCorrect = current.selectedOptionIndex == question.correctIndex
        val newScore = if (isCorrect) current.score + 20 else current.score

        _quizState.value = current.copy(
            isSubmitted = true,
            score = newScore
        )
    }

    fun nextQuizQuestion() {
        val current = _quizState.value
        if (current.currentQuestionIndex + 1 < quizQuestions.size) {
            _quizState.value = current.copy(
                currentQuestionIndex = current.currentQuestionIndex + 1,
                selectedOptionIndex = null,
                isSubmitted = false
            )
        } else {
            _quizState.value = current.copy(isCompleted = true)
        }
    }

    fun resetQuiz() {
        _quizState.value = QuizState()
    }
}

class KomsosViewModelFactory(
    private val application: Application,
    private val currentUserId: String,
    private val databaseId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KomsosViewModel(application, currentUserId, databaseId) as T
    }
}
