package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.GameScoreEntity
import com.example.data.local.HabitEntity
import com.example.data.local.NoteEntity
import com.example.data.local.SavedTrendEntity
import com.example.data.repository.OmniRepository
import com.example.data.repository.TrendItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class HubTab(val title: String) {
    TREND_PULSE("Trends & AI"),
    WORKSPACE("Workspace"),
    CREATIVE("Creative Studio"),
    ARCADE("Minigames"),
    DASHBOARD("Overview")
}

enum class WorkspaceMode {
    NOTES, HABITS, CALCULATOR, POMODORO
}

enum class GameType {
    MENU, TRIVIA, MEMORY, REFLEX, WORD
}

class OmniViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OmniRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = OmniRepository(database.omniDao())
        seedInitialDataIfEmpty()
    }

    // UI States
    private val _selectedTab = MutableStateFlow(HubTab.TREND_PULSE)
    val selectedTab: StateFlow<HubTab> = _selectedTab.asStateFlow()

    private val _omniPrompt = MutableStateFlow("")
    val omniPrompt: StateFlow<String> = _omniPrompt.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiResult = MutableStateFlow<String?>(null)
    val aiResult: StateFlow<String?> = _aiResult.asStateFlow()

    private val _workspaceMode = MutableStateFlow(WorkspaceMode.NOTES)
    val workspaceMode: StateFlow<WorkspaceMode> = _workspaceMode.asStateFlow()

    private val _activeGame = MutableStateFlow(GameType.MENU)
    val activeGame: StateFlow<GameType> = _activeGame.asStateFlow()

    // Room Data Flows
    val notes: StateFlow<List<NoteEntity>> = repository.notes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val habits: StateFlow<List<HabitEntity>> = repository.habits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val gameScores: StateFlow<List<GameScoreEntity>> = repository.gameScores.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedTrends: StateFlow<List<SavedTrendEntity>> = repository.savedTrends.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Calculator & Utility States
    private val _calcExpression = MutableStateFlow("")
    val calcExpression: StateFlow<String> = _calcExpression.asStateFlow()

    private val _calcResult = MutableStateFlow("0")
    val calcResult: StateFlow<String> = _calcResult.asStateFlow()

    // Pomodoro Timer State
    private val _timerSeconds = MutableStateFlow(25 * 60)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    // Minigame States
    private val _currentTopic = MutableStateFlow("AI & Future Tech")
    val currentTopic: StateFlow<String> = _currentTopic.asStateFlow()

    private val _isGeneratingContextualGame = MutableStateFlow(false)
    val isGeneratingContextualGame: StateFlow<Boolean> = _isGeneratingContextualGame.asStateFlow()

    private val _contextualTrivia = MutableStateFlow<List<com.example.data.repository.TriviaQuestion>>(emptyList())
    val contextualTrivia: StateFlow<List<com.example.data.repository.TriviaQuestion>> = _contextualTrivia.asStateFlow()

    private val _triviaScore = MutableStateFlow(0)
    val triviaScore: StateFlow<Int> = _triviaScore.asStateFlow()

    private val _triviaCurrentQuestionIndex = MutableStateFlow(0)
    val triviaCurrentQuestionIndex: StateFlow<Int> = _triviaCurrentQuestionIndex.asStateFlow()

    fun setCurrentTopic(topic: String) {
        if (topic.isNotBlank()) {
            _currentTopic.value = topic
        }
    }

    fun launchContextualMinigameForTopic(topic: String) {
        _currentTopic.value = topic
        _selectedTab.value = HubTab.ARCADE
        _activeGame.value = GameType.TRIVIA
        generateContextualQuestions(topic)
    }

    fun generateContextualQuestions(topic: String = _currentTopic.value) {
        _isGeneratingContextualGame.value = true
        _triviaCurrentQuestionIndex.value = 0
        _triviaScore.value = 0

        viewModelScope.launch {
            val prompt = """
                Generate 3 fun trivia questions about the topic '$topic'.
                Format each question strictly as:
                Q: Question Text
                A) Option 1
                B) Option 2
                C) Option 3
                D) Option 4
                CORRECT: A (or B, C, D)
                EXPLANATION: Short explanation
                ---
            """.trimIndent()

            val rawResult = repository.queryGemini(prompt)
            val parsedQuestions = parseTriviaResponse(rawResult, topic)
            _contextualTrivia.value = parsedQuestions
            _isGeneratingContextualGame.value = false
        }
    }

    private fun parseTriviaResponse(raw: String, topic: String): List<com.example.data.repository.TriviaQuestion> {
        val list = mutableListOf<com.example.data.repository.TriviaQuestion>()
        val blocks = raw.split("---")
        for (block in blocks) {
            val lines = block.lines().map { it.trim() }.filter { it.isNotEmpty() }
            var qText = ""
            val options = mutableListOf<String>()
            var correctIdx = 0
            var explanation = "Contextual trivia on $topic."

            for (line in lines) {
                when {
                    line.startsWith("Q:") -> qText = line.removePrefix("Q:").trim()
                    line.startsWith("A)") -> options.add(line.removePrefix("A)").trim())
                    line.startsWith("B)") -> options.add(line.removePrefix("B)").trim())
                    line.startsWith("C)") -> options.add(line.removePrefix("C)").trim())
                    line.startsWith("D)") -> options.add(line.removePrefix("D)").trim())
                    line.startsWith("CORRECT:") -> {
                        val char = line.removePrefix("CORRECT:").trim().uppercase().firstOrNull() ?: 'A'
                        correctIdx = when (char) {
                            'A' -> 0
                            'B' -> 1
                            'C' -> 2
                            'D' -> 3
                            else -> 0
                        }
                    }
                    line.startsWith("EXPLANATION:") -> explanation = line.removePrefix("EXPLANATION:").trim()
                }
            }

            if (qText.isNotEmpty() && options.size >= 2) {
                while (options.size < 4) options.add("Option ${options.size + 1}")
                list.add(com.example.data.repository.TriviaQuestion(qText, options, correctIdx, explanation))
            }
        }

        if (list.isEmpty()) {
            list.addAll(getFallbackQuestions(topic))
        }
        return list
    }

    private fun getFallbackQuestions(topic: String): List<com.example.data.repository.TriviaQuestion> {
        return listOf(
            com.example.data.repository.TriviaQuestion(
                question = "In the domain of '$topic', what is a primary driving factor for rapid innovation?",
                options = listOf("Continuous AI adaptation", "Static legacy code", "Manual paper records", "Offline batch processing"),
                correctIndex = 0,
                explanation = "Adaptive real-time feedback accelerates progress in $topic."
            ),
            com.example.data.repository.TriviaQuestion(
                question = "Which concept best complements research in '$topic'?",
                options = listOf("Micro-habit feedback loops", "Ignoring user data", "Unstructured random noise", "Fixed static templates"),
                correctIndex = 0,
                explanation = "Micro-habit loops enhance retention and mastery of $topic."
            ),
            com.example.data.repository.TriviaQuestion(
                question = "How does OmniHub AI personalize learning for '$topic'?",
                options = listOf("By dynamically tailoring minigames & notes", "By disabling games", "By freezing user state", "By showing generic static text"),
                correctIndex = 0,
                explanation = "OmniHub AI tailors games to active user tasks!"
            )
        )
    }

    // Reflex Game
    private val _reflexState = MutableStateFlow("WAIT") // WAIT, READY, TAP, FINISHED
    val reflexState: StateFlow<String> = _reflexState.asStateFlow()

    private val _reflexReactionTimeMs = MutableStateFlow(0L)
    val reflexReactionTimeMs: StateFlow<Long> = _reflexReactionTimeMs.asStateFlow()

    private var reflexStartTime = 0L

    fun setTab(tab: HubTab) {
        _selectedTab.value = tab
    }

    fun setOmniPrompt(text: String) {
        _omniPrompt.value = text
    }

    fun setWorkspaceMode(mode: WorkspaceMode) {
        _workspaceMode.value = mode
    }

    fun setActiveGame(game: GameType) {
        _activeGame.value = game
        if (game == GameType.REFLEX) {
            resetReflexGame()
        }
    }

    // Execute Adaptive Unified Prompt
    fun executeOmniPrompt(promptText: String = _omniPrompt.value) {
        if (promptText.isBlank()) return
        val text = promptText.trim()
        _isAiLoading.value = true
        _aiResult.value = null

        // Adapt UI Tab based on prompt keywords
        val lower = text.lowercase()
        when {
            lower.contains("game") || lower.contains("play") || lower.contains("trivia") -> {
                _selectedTab.value = HubTab.ARCADE
            }
            lower.contains("note") || lower.contains("habit") || lower.contains("calc") || lower.contains("timer") -> {
                _selectedTab.value = HubTab.WORKSPACE
            }
            lower.contains("prompt") || lower.contains("creative") || lower.contains("caption") -> {
                _selectedTab.value = HubTab.CREATIVE
            }
            lower.contains("trend") || lower.contains("radar") -> {
                _selectedTab.value = HubTab.TREND_PULSE
            }
        }

        viewModelScope.launch {
            val response = repository.queryGemini(
                prompt = text,
                systemInstruction = "You are OmniHub AI, an adaptive all-in-one assistant. Provide concise, ultra-engaging, direct answers."
            )
            _aiResult.value = response
            _isAiLoading.value = false
        }
    }

    fun clearAiResult() {
        _aiResult.value = null
    }

    // Notes Actions
    fun addNote(title: String, content: String, category: String = "General") {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            val summary = if (content.length > 50) {
                repository.queryGemini("Summarize in 1 bullet point: $content")
            } else ""
            repository.saveNote(title.ifBlank { "Untitled Note" }, content, category, summary)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
        }
    }

    // Habits Actions
    fun addHabit(title: String, category: String = "Daily") {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addHabit(title, category)
        }
    }

    fun toggleHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.toggleHabit(habit)
        }
    }

    fun deleteHabit(id: Long) {
        viewModelScope.launch {
            repository.deleteHabit(id)
        }
    }

    // Trend Actions
    fun saveTrendItem(trend: TrendItem) {
        viewModelScope.launch {
            repository.saveTrend(trend)
        }
    }

    fun deleteSavedTrend(id: Long) {
        viewModelScope.launch {
            repository.deleteTrend(id)
        }
    }

    // Calculator Actions
    fun onCalcInput(char: String) {
        when (char) {
            "C" -> {
                _calcExpression.value = ""
                _calcResult.value = "0"
            }
            "=" -> {
                calculateResult()
            }
            "DEL" -> {
                if (_calcExpression.value.isNotEmpty()) {
                    _calcExpression.value = _calcExpression.value.dropLast(1)
                }
            }
            else -> {
                _calcExpression.value += char
            }
        }
    }

    private fun calculateResult() {
        val expr = _calcExpression.value
        if (expr.isBlank()) return
        try {
            // Simple robust math evaluation
            val sanitized = expr.replace("×", "*").replace("÷", "/")
            val res = evaluateSimpleMath(sanitized)
            _calcResult.value = if (res % 1.0 == 0.0) res.toLong().toString() else "%.2f".format(res)
        } catch (e: Exception) {
            _calcResult.value = "Error"
        }
    }

    private fun evaluateSimpleMath(expression: String): Double {
        val tokens = expression.split(Regex("(?<=[-+*/])|(?=[-+*/])")).map { it.trim() }
        if (tokens.isEmpty()) return 0.0
        var total = tokens[0].toDoubleOrNull() ?: 0.0
        var idx = 1
        while (idx < tokens.size - 1) {
            val op = tokens[idx]
            val nextVal = tokens[idx + 1].toDoubleOrNull() ?: 0.0
            when (op) {
                "+" -> total += nextVal
                "-" -> total -= nextVal
                "*" -> total *= nextVal
                "/" -> if (nextVal != 0.0) total /= nextVal
            }
            idx += 2
        }
        return total
    }

    // Pomodoro Actions
    fun toggleTimer() {
        if (_isTimerRunning.value) {
            timerJob?.cancel()
            _isTimerRunning.value = false
        } else {
            _isTimerRunning.value = true
            timerJob = viewModelScope.launch {
                while (_timerSeconds.value > 0) {
                    delay(1000)
                    _timerSeconds.value -= 1
                }
                _isTimerRunning.value = false
            }
        }
    }

    fun resetTimer(minutes: Int = 25) {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _timerSeconds.value = minutes * 60
    }

    // Minigame Trivia Actions
    fun answerTrivia(selectedIndex: Int, correctIndex: Int) {
        if (selectedIndex == correctIndex) {
            _triviaScore.value += 100
        }
        _triviaCurrentQuestionIndex.value += 1
        viewModelScope.launch {
            repository.updateGameScore("trivia", _triviaScore.value)
        }
    }

    fun resetTrivia() {
        _triviaScore.value = 0
        _triviaCurrentQuestionIndex.value = 0
    }

    // Reflex Game Actions
    fun resetReflexGame() {
        _reflexState.value = "WAIT"
        _reflexReactionTimeMs.value = 0L
    }

    fun startReflexCountdown() {
        _reflexState.value = "READY"
        viewModelScope.launch {
            val randomDelay = (2000..4500).random().toLong()
            delay(randomDelay)
            if (_reflexState.value == "READY") {
                _reflexState.value = "TAP"
                reflexStartTime = System.currentTimeMillis()
            }
        }
    }

    fun onReflexTap() {
        val currentState = _reflexState.value
        if (currentState == "READY") {
            _reflexState.value = "TOO_EARLY"
        } else if (currentState == "TAP") {
            val reaction = System.currentTimeMillis() - reflexStartTime
            _reflexReactionTimeMs.value = reaction
            _reflexState.value = "FINISHED"
            val score = maxOf(10, 1000 - reaction.toInt())
            viewModelScope.launch {
                repository.updateGameScore("reflex", score)
            }
        }
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            delay(300)
            if (habits.value.isEmpty()) {
                repository.addHabit("⚡ Explore Daily AI Trends", "Daily")
                repository.addHabit("🎯 25-Min Focus Session", "Productivity")
                repository.addHabit("🎮 Play AI Trivia Challenge", "Gaming")
            }
            if (notes.value.isEmpty()) {
                repository.saveNote(
                    title = "🚀 Welcome to OmniHub AI",
                    content = "OmniHub AI merges trends, dynamic tools, creative studio, and minigames into one unified experience. Type any command in the top prompt bar to begin!",
                    category = "Guide",
                    aiSummary = "OmniHub AI is your all-in-one AI companion."
                )
            }
        }
    }
}
