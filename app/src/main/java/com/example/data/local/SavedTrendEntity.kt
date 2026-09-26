package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_trends")
data class SavedTrendEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val popularityScore: Int = 90,
    val aiInsight: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
