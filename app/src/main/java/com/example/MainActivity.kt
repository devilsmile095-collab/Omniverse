package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.OmniBottomNav
import com.example.ui.components.OmniPromptBar
import com.example.ui.components.OmniTopBar
import com.example.ui.screens.ArcadeScreen
import com.example.ui.screens.CreativeScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.TrendPulseScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.OmniHubTheme
import com.example.ui.viewmodel.HubTab
import com.example.ui.viewmodel.OmniViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniHubTheme {
                OmniHubApp()
            }
        }
    }
}

@Composable
fun OmniHubApp(viewModel: OmniViewModel = viewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val omniPrompt by viewModel.omniPrompt.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiResult by viewModel.aiResult.collectAsStateWithLifecycle()
    val workspaceMode by viewModel.workspaceMode.collectAsStateWithLifecycle()
    val activeGame by viewModel.activeGame.collectAsStateWithLifecycle()

    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val gameScores by viewModel.gameScores.collectAsStateWithLifecycle()
    val savedTrends by viewModel.savedTrends.collectAsStateWithLifecycle()

    val calcExpression by viewModel.calcExpression.collectAsStateWithLifecycle()
    val calcResult by viewModel.calcResult.collectAsStateWithLifecycle()

    val timerSeconds by viewModel.timerSeconds.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()

    val triviaScore by viewModel.triviaScore.collectAsStateWithLifecycle()
    val triviaIndex by viewModel.triviaCurrentQuestionIndex.collectAsStateWithLifecycle()

    val reflexState by viewModel.reflexState.collectAsStateWithLifecycle()
    val reflexReactionMs by viewModel.reflexReactionTimeMs.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground),
        containerColor = CyberBackground,
        topBar = {
            Column {
                OmniTopBar(
                    title = selectedTab.title,
                    isAiActive = isAiLoading
                )
                OmniPromptBar(
                    promptValue = omniPrompt,
                    onValueChange = viewModel::setOmniPrompt,
                    onSubmit = viewModel::executeOmniPrompt,
                    aiResultText = aiResult,
                    onClearResult = viewModel::clearAiResult
                )
            }
        },
        bottomBar = {
            OmniBottomNav(
                selectedTab = selectedTab,
                onTabSelected = viewModel::setTab
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                HubTab.TREND_PULSE -> TrendPulseScreen(
                    onQuickPrompt = viewModel::executeOmniPrompt,
                    onSaveTrend = viewModel::saveTrendItem
                )
                HubTab.WORKSPACE -> WorkspaceScreen(
                    currentMode = workspaceMode,
                    onModeChange = viewModel::setWorkspaceMode,
                    notes = notes,
                    habits = habits,
                    onAddNote = viewModel::addNote,
                    onDeleteNote = viewModel::deleteNote,
                    onAddHabit = viewModel::addHabit,
                    onToggleHabit = viewModel::toggleHabit,
                    onDeleteHabit = viewModel::deleteHabit,
                    calcExpr = calcExpression,
                    calcResult = calcResult,
                    onCalcInput = viewModel::onCalcInput,
                    timerSeconds = timerSeconds,
                    isTimerRunning = isTimerRunning,
                    onToggleTimer = viewModel::toggleTimer,
                    onResetTimer = viewModel::resetTimer
                )
                HubTab.CREATIVE -> CreativeScreen(
                    onGeneratePrompt = viewModel::executeOmniPrompt
                )
                HubTab.ARCADE -> ArcadeScreen(
                    activeGame = activeGame,
                    onSelectGame = viewModel::setActiveGame,
                    gameScores = gameScores,
                    triviaScore = triviaScore,
                    triviaIndex = triviaIndex,
                    onAnswerTrivia = viewModel::answerTrivia,
                    onResetTrivia = viewModel::resetTrivia,
                    reflexState = reflexState,
                    reflexTimeMs = reflexReactionMs,
                    onStartReflex = viewModel::startReflexCountdown,
                    onReflexTap = viewModel::onReflexTap,
                    onResetReflex = viewModel::resetReflexGame
                )
                HubTab.DASHBOARD -> DashboardScreen(
                    savedTrends = savedTrends,
                    habits = habits,
                    notes = notes,
                    gameScores = gameScores,
                    onDeleteSavedTrend = viewModel::deleteSavedTrend
                )
            }
        }
    }
}
