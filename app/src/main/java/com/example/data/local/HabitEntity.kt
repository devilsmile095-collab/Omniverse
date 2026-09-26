package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Daily",
    val isCompletedToday: Boolean = false,
    val streakCount: Int = 0,
    val lastUpdatedDate: String = ""
)
