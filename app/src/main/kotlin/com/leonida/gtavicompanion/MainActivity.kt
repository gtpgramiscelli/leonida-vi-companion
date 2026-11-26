package com.leonida.gtavicompanion

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leonida.gtavicompanion.data.GitHubAppUpdater
import com.leonida.gtavicompanion.data.SharedPrefsProgressRepository
import com.leonida.gtavicompanion.ui.CompanionViewModel
import com.leonida.gtavicompanion.ui.LeonidaApp
import com.leonida.gtavicompanion.ui.theme.LeonidaGuideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val repository = SharedPrefsProgressRepository(applicationContext)
        val updater = GitHubAppUpdater(applicationContext)

        setContent {
            LeonidaGuideTheme {
                val companionViewModel: CompanionViewModel = viewModel(
                    factory = CompanionViewModel.factory(repository, updater)
                )
                LeonidaApp(viewModel = companionViewModel)
            }
        }
    }
}
