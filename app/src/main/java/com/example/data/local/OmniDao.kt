package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OmniDao {
    // Notes
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    // Habits
    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: Long)

    // Game Scores
    @Query("SELECT * FROM game_scores")
    fun getAllGameScores(): Flow<List<GameScoreEntity>>

    @Query("SELECT * FROM game_scores WHERE gameType = :gameType")
    suspend fun getGameScore(gameType: String): GameScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGameScore(score: GameScoreEntity)

    // Saved Trends
    @Query("SELECT * FROM saved_trends ORDER BY timestamp DESC")
    fun getAllSavedTrends(): Flow<List<SavedTrendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrend(trend: SavedTrendEntity): Long

    @Query("DELETE FROM saved_trends WHERE id = :trendId")
    suspend fun deleteTrendById(trendId: Long)
}
