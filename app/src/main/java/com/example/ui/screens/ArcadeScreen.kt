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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.GameScoreEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameType

val sampleTriviaQuestions = listOf(
    com.example.data.repository.TriviaQuestion(
        question = "Which tech paradigm combines AI agents, real-time context, and automated task execution?",
        options = listOf("Autonomous Agent Workflows", "Static HTML Pages", "Batch Processing", "Legacy Mainframes"),
        correctIndex = 0,
        explanation = "Autonomous Agent Workflows allow AI models to perform complex multi-step reasoning."
    ),
    com.example.data.repository.TriviaQuestion(
        question = "What is the primary benefit of zero-latency local LLM inference on mobile devices?",
        options = listOf("Complete offline privacy and instant response", "Higher server hosting costs", "Slower processing speeds", "Mandatory cloud streaming"),
        correctIndex = 0,
        explanation = "On-device models run locally without sending sensitive user data to external servers."
    ),
    com.example.data.repository.TriviaQuestion(
        question = "How does micro-habit gamification boost daily user retention?",
        options = listOf("By turning routines into interactive RPG challenges", "By removing all progress tracking", "By sending 100 spam emails daily", "By locking the screen randomly"),
        correctIndex = 0,
        explanation = "Gamification unlocks achievements and mini-game bonuses when habits are completed."
    )
)

@Composable
fun ArcadeScreen(
    activeGame: GameType,
    onSelectGame: (GameType) -> Unit,
    gameScores: List<GameScoreEntity>,
    triviaScore: Int,
    triviaIndex: Int,
    onAnswerTrivia: (Int, Int) -> Unit,
    onResetTrivia: () -> Unit,
    reflexState: String,
    reflexTimeMs: Long,
    onStartReflex: () -> Unit,
    onReflexTap: () -> Unit,
    onResetReflex: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (activeGame == GameType.MENU) {
            ArcadeMenu(onSelectGame = onSelectGame, gameScores = gameScores)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onSelectGame(GameType.MENU) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    text = when (activeGame) {
                        GameType.TRIVIA -> "Brain AI Trivia Challenge"
                        GameType.REFLEX -> "Reflex Rush Challenge"
                        GameType.WORD -> "Cyber Word Scramble"
                        else -> "Minigames Arcade"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (activeGame) {
                GameType.TRIVIA -> TriviaGameView(triviaScore, triviaIndex, onAnswerTrivia, onResetTrivia)
                GameType.REFLEX -> ReflexGameView(reflexState, reflexTimeMs, onStartReflex, onReflexTap, onResetReflex)
                GameType.WORD -> WordScrambleView()
                else -> {}
            }
        }
    }
}

@Composable
fun ArcadeMenu(
    onSelectGame: (GameType) -> Unit,
    gameScores: List<GameScoreEntity>
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "OmniHub Context Arcade", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        Text(text = "Contextual minigames adapt to your research & active trends!", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
        }

        item {
            GameCard(
                title = "🧠 AI Trend Trivia",
                subtitle = "Test your knowledge on viral technology & AI agent trends",
                highScore = gameScores.find { it.gameType == "trivia" }?.highScore ?: 0,
                color = NeonPurple,
                onClick = { onSelectGame(GameType.TRIVIA) },
                tag = "game_card_trivia"
            )
        }

        item {
            GameCard(
                title = "⚡ Reflex Rush",
                subtitle = "Test your reaction speed in milliseconds when neon signal fires",
                highScore = gameScores.find { it.gameType == "reflex" }?.highScore ?: 0,
                color = NeonPink,
                onClick = { onSelectGame(GameType.REFLEX) },
                tag = "game_card_reflex"
            )
        }

        item {
            GameCard(
                title = "🔤 Cyber Word Scramble",
                subtitle = "Unscramble trending technological and productivity terms",
                highScore = 150,
                color = CyanPrimary,
                onClick = { onSelectGame(GameType.WORD) },
                tag = "game_card_word"
            )
        }
    }
}

@Composable
fun GameCard(
    title: String,
    subtitle: String,
    highScore: Int,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$highScore PTS", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = NeonAmber)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun TriviaGameView(
    score: Int,
    currentIndex: Int,
    onAnswer: (Int, Int) -> Unit,
    onReset: () -> Unit
) {
    if (currentIndex >= sampleTriviaQuestions.size) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Trivia Challenge Complete!", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Final Score: $score PTS", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = CyanAccent)
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onReset, colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)) {
                    Text("Play Again", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        val q = sampleTriviaQuestions[currentIndex]
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Question ${currentIndex + 1}/${sampleTriviaQuestions.size}", style = MaterialTheme.typography.labelMedium, color = CyanAccent)
                    Text(text = "Score: $score", style = MaterialTheme.typography.labelMedium, color = NeonAmber)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = q.question, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)

                Spacer(modifier = Modifier.height(16.dp))

                q.options.forEachIndexed { idx, opt ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberSurfaceVariant)
                            .clickable { onAnswer(idx, q.correctIndex) }
                            .padding(14.dp)
                    ) {
                        Text(text = "${'A' + idx}. $opt", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ReflexGameView(
    state: String,
    timeMs: Long,
    onStart: () -> Unit,
    onTap: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (state) {
                "TAP" -> NeonGreen
                "TOO_EARLY" -> NeonPink
                else -> CyberSurface
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable {
                when (state) {
                    "WAIT", "FINISHED", "TOO_EARLY" -> onStart()
                    "READY", "TAP" -> onTap()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (state) {
                "WAIT" -> {
                    Text(text = "⚡ TAP TO START", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tap as soon as the screen turns GREEN!", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
                "READY" -> {
                    Text(text = "🔴 WAIT FOR GREEN...", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = NeonPink)
                }
                "TAP" -> {
                    Text(text = "🟢 TAP NOW!", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = TextPrimary)
                }
                "FINISHED" -> {
                    Text(text = "⚡ Reaction Time", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "$timeMs ms", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = CyanAccent)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Tap anywhere to try again", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                "TOO_EARLY" -> {
                    Text(text = "❌ TOO EARLY!", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tap to restart", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun WordScrambleView() {
    var userGuess by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
    val scrambledWords = listOf("AGENTS" to "GSAETN", "HABIT" to "BHIAT", "PULSE" to "LESUP")
    var currentPairIndex by remember { mutableStateOf(0) }

    val currentPair = scrambledWords[currentPairIndex % scrambledWords.size]

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Unscramble Trend Word:", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentPair.second, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 4.sp), color = CyanAccent)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = userGuess,
                onValueChange = { userGuess = it },
                placeholder = { Text("Your Answer...", color = TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanAccent, unfocusedBorderColor = CyberSurfaceVariant, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (userGuess.trim().equals(currentPair.first, ignoreCase = true)) {
                        score += 50
                        userGuess = ""
                        currentPairIndex++
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Answer ($score PTS)", fontWeight = FontWeight.Bold)
            }
        }
    }
}
