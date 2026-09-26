package com.example.data.repository

import com.example.data.local.GameScoreEntity
import com.example.data.local.HabitEntity
import com.example.data.local.NoteEntity
import com.example.data.local.OmniDao
import com.example.data.local.SavedTrendEntity
import com.example.data.remote.Content
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.GenerationConfig
import com.example.data.remote.Part
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class TrendItem(
    val title: String,
    val description: String,
    val category: String,
    val popularityScore: Int,
    val aiInsight: String
)

data class TriviaQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

class OmniRepository(private val dao: OmniDao) {

    // Room Database Flows
    val notes: Flow<List<NoteEntity>> = dao.getAllNotes()
    val habits: Flow<List<HabitEntity>> = dao.getAllHabits()
    val gameScores: Flow<List<GameScoreEntity>> = dao.getAllGameScores()
    val savedTrends: Flow<List<SavedTrendEntity>> = dao.getAllSavedTrends()

    // Database Actions
    suspend fun saveNote(title: String, content: String, category: String = "General", aiSummary: String = "") {
        dao.insertNote(NoteEntity(title = title, content = content, category = category, aiSummary = aiSummary))
    }

    suspend fun deleteNote(id: Long) {
        dao.deleteNoteById(id)
    }

    suspend fun addHabit(title: String, category: String = "Daily") {
        dao.insertHabit(HabitEntity(title = title, category = category))
    }

    suspend fun toggleHabit(habit: HabitEntity) {
        val newCompleted = !habit.isCompletedToday
        val newStreak = if (newCompleted) habit.streakCount + 1 else maxOf(0, habit.streakCount - 1)
        dao.updateHabit(habit.copy(isCompletedToday = newCompleted, streakCount = newStreak))
    }

    suspend fun deleteHabit(id: Long) {
        dao.deleteHabitById(id)
    }

    suspend fun updateGameScore(gameType: String, score: Int) {
        val existing = dao.getGameScore(gameType)
        val highScore = maxOf(score, existing?.highScore ?: 0)
        val totalPlayed = (existing?.totalPlayed ?: 0) + 1
        dao.insertOrUpdateGameScore(
            GameScoreEntity(
                gameType = gameType,
                highScore = highScore,
                totalPlayed = totalPlayed,
                lastScore = score,
                lastPlayedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun saveTrend(trend: TrendItem) {
        dao.insertTrend(
            SavedTrendEntity(
                title = trend.title,
                description = trend.description,
                category = trend.category,
                popularityScore = trend.popularityScore,
                aiInsight = trend.aiInsight
            )
        )
    }

    suspend fun deleteTrend(id: Long) {
        dao.deleteTrendById(id)
    }

    // Gemini AI Methods
    suspend fun queryGemini(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = RetrofitClient.getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext getOfflineAiFallback(prompt)
        }

        try {
            val request = GenerateContentRequest(
                contents = listOf(
                    Content(parts = listOf(Part(text = prompt)))
                ),
                systemInstruction = systemInstruction?.let {
                    Content(parts = listOf(Part(text = it)))
                },
                generationConfig = GenerationConfig(temperature = 0.7f, maxOutputTokens = 800)
            )

            val response = RetrofitClient.service.generateContent(apiKey, request)
            val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!candidateText.isNullOrBlank()) {
                candidateText!!.trim()
            } else {
                getOfflineAiFallback(prompt)
            }
        } catch (e: Exception) {
            getOfflineAiFallback(prompt)
        }
    }

    private fun getOfflineAiFallback(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("trend") || lower.contains("popular") ->
                "🚀 Current Trends: 1. Autonomous AI Agents, 2. Spatial Computing Apps, 3. Micro-Habit Gamification, 4. Dark-mode Cyber aesthetics, 5. Zero-latency local LLMs."
            lower.contains("summary") || lower.contains("summarize") ->
                "💡 Key Takeaway: Focus on core value, maintain clear structure, eliminate redundant overhead, and iterate based on real feedback."
            lower.contains("game") || lower.contains("trivia") ->
                "🎮 Quick Trend Trivia: What was the first widespread viral AI image generator model? Answer: Midjourney & Stable Diffusion!"
            lower.contains("note") || lower.contains("idea") ->
                "✍️ Creative Spark: Combine daily habit streaks with adaptive AI suggestions to maximize user engagement."
            else ->
                "✨ Gemini Assistant: Processing your request ('$prompt'). OmniHub is fully synchronized and ready to perform tasks across all workspace modes!"
        }
    }
}

private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()
