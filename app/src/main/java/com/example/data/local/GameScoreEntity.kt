package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey val gameType: String, // e.g. "trivia", "memory", "reflex", "word"
    val highScore: Int = 0,
    val totalPlayed: Int = 0,
    val lastScore: Int = 0,
    val lastPlayedTimestamp: Long = System.currentTimeMillis()
)
