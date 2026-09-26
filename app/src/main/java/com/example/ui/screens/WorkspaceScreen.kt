package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.HabitEntity
import com.example.data.local.NoteEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.WorkspaceMode

@Composable
fun WorkspaceScreen(
    currentMode: WorkspaceMode,
    onModeChange: (WorkspaceMode) -> Unit,
    notes: List<NoteEntity>,
    habits: List<HabitEntity>,
    onAddNote: (String, String, String) -> Unit,
    onDeleteNote: (Long) -> Unit,
    onAddHabit: (String, String) -> Unit,
    onToggleHabit: (HabitEntity) -> Unit,
    onDeleteHabit: (Long) -> Unit,
    calcExpr: String,
    calcResult: String,
    onCalcInput: (String) -> Unit,
    timerSeconds: Int,
    isTimerRunning: Boolean,
    onToggleTimer: () -> Unit,
    onResetTimer: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        WorkspaceMode.NOTES to "Notes",
        WorkspaceMode.HABITS to "Habits",
        WorkspaceMode.CALCULATOR to "Calc",
        WorkspaceMode.POMODORO to "Focus"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Workspace Tab Selector Bar
        TabRow(
            selectedTabIndex = modes.indexOfFirst { it.first == currentMode }.coerceAtLeast(0),
            containerColor = CyberSurfaceVariant,
            contentColor = CyanAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[modes.indexOfFirst { it.first == currentMode }.coerceAtLeast(0)]),
                    color = CyanAccent
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(width = 1.dp, color = CyanPrimary.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp))
        ) {
            modes.forEach { (mode, title) ->
                Tab(
                    selected = currentMode == mode,
                    onClick = { onModeChange(mode) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (currentMode == mode) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = if (currentMode == mode) CyanAccent else TextSecondary
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (currentMode) {
            WorkspaceMode.NOTES -> NotesView(notes, onAddNote, onDeleteNote)
            WorkspaceMode.HABITS -> HabitsView(habits, onAddHabit, onToggleHabit, onDeleteHabit)
            WorkspaceMode.CALCULATOR -> CalculatorView(calcExpr, calcResult, onCalcInput)
            WorkspaceMode.POMODORO -> PomodoroView(timerSeconds, isTimerRunning, onToggleTimer, onResetTimer)
        }
    }
}

@Composable
fun NotesView(
    notes: List<NoteEntity>,
    onAddNote: (String, String, String) -> Unit,
    onDeleteNote: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "✍️ Create AI Note",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Note Title", color = TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CyberSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_title_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        placeholder = { Text("Note content or research details...", color = TextSecondary) },
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = CyberSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_content_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onAddNote(title, content, category)
                            title = ""
                            content = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_note_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Note & Generate AI Summary", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(notes) { note ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        IconButton(onClick = { onDeleteNote(note.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = NeonPink, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = note.content, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    if (note.aiSummary.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurfaceVariant)
                                .padding(8.dp)
                        ) {
                            Text(text = "💡 AI Summary: ${note.aiSummary}", style = MaterialTheme.typography.bodySmall, color = CyanAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitsView(
    habits: List<HabitEntity>,
    onAddHabit: (String, String) -> Unit,
    onToggleHabit: (HabitEntity) -> Unit,
    onDeleteHabit: (Long) -> Unit
) {
    var habitTitle by remember { mutableStateOf("") }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = habitTitle,
                    onValueChange = { habitTitle = it },
                    placeholder = { Text("New Daily Habit (e.g. Read 15 mins)", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = CyberSurfaceVariant,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("habit_title_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (habitTitle.isNotBlank()) {
                            onAddHabit(habitTitle, "Daily")
                            habitTitle = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    modifier = Modifier.testTag("add_habit_button")
                ) {
                    Text("Add", fontWeight = FontWeight.Bold)
                }
            }
        }

        items(habits) { habit ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Checkbox(
                            checked = habit.isCompletedToday,
                            onCheckedChange = { onToggleHabit(habit) },
                            colors = CheckboxDefaults.colors(checkedColor = NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = habit.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (habit.isCompletedToday) TextSecondary else TextPrimary
                            )
                            Text(
                                text = "🔥 Streak: ${habit.streakCount} days",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonGreen
                            )
                        }
                    }

                    IconButton(onClick = { onDeleteHabit(habit.id) }, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Habit", tint = NeonPink, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorView(
    expression: String,
    result: String,
    onInput: (String) -> Unit
) {
    val buttons = listOf(
        listOf("C", "DEL", "÷", "×"),
        listOf("7", "8", "9", "-"),
        listOf("4", "5", "6", "+"),
        listOf("1", "2", "3", "="),
        listOf("0", ".", "", "")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(text = expression.ifEmpty { "0" }, style = MaterialTheme.typography.titleLarge, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = result, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = CyanAccent)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { btn ->
                        if (btn.isNotEmpty()) {
                            Button(
                                onClick = { onInput(btn) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .testTag("calc_btn_$btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when (btn) {
                                        "=" -> CyanPrimary
                                        "C", "DEL" -> NeonPink.copy(alpha = 0.8f)
                                        "+", "-", "×", "÷" -> NeonPurple
                                        else -> CyberSurfaceVariant
                                    }
                                )
                            ) {
                                Text(text = btn, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PomodoroView(
    timerSeconds: Int,
    isTimerRunning: Boolean,
    onToggleTimer: () -> Unit,
    onResetTimer: (Int) -> Unit
) {
    val minutes = timerSeconds / 60
    val seconds = timerSeconds % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(CyberSurface)
                .border(width = 4.dp, brush = Brush.sweepGradient(listOf(CyanPrimary, NeonPurple, CyanAccent)), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 44.sp, fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(text = if (isTimerRunning) "FOCUS MODE" else "PAUSED", style = MaterialTheme.typography.labelMedium, color = CyanAccent)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onToggleTimer,
                modifier = Modifier
                    .width(140.dp)
                    .height(48.dp)
                    .testTag("toggle_timer_button"),
                colors = ButtonDefaults.buttonColors(containerColor = if (isTimerRunning) NeonPink else CyanPrimary)
            ) {
                Icon(imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isTimerRunning) "Pause" else "Start", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onResetTimer(25) },
                modifier = Modifier
                    .width(100.dp)
                    .height(48.dp)
                    .testTag("reset_timer_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = TextPrimary)
            }
        }
    }
}
