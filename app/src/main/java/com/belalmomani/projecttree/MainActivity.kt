package com.belalmomani.projecttree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belalmomani.projecttree.form.FormViewModel
import com.belalmomani.projecttree.form.ImportViewModel
import com.belalmomani.projecttree.ui.FormScreen
import com.belalmomani.projecttree.ui.HomeScreen
import com.belalmomani.projecttree.ui.ImportScreen
import com.belalmomani.projecttree.ui.PromptScreen
import com.belalmomani.projecttree.ui.ProjectTreeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectTreeTheme {
                // واجهة عربية: اتجاه RTL دائمًا
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        AppNav()
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun AppNav() {
    val vm: FormViewModel = viewModel()
    val importVm: ImportViewModel = viewModel()
    var screen by rememberSaveable { mutableStateOf("home") }

    BackHandler(enabled = screen != "home") {
        screen = if (screen == "prompt") "form" else "home"
    }

    when (screen) {
        "form" -> FormScreen(
            vm = vm,
            onBack = { screen = "home" },
            onContinue = {
                vm.generatePrompt()
                screen = "prompt"
            },
        )
        "prompt" -> PromptScreen(
            vm = vm,
            onEdit = { screen = "form" },
            onImport = { screen = "import" },
        )
        "import" -> ImportScreen(vm = importVm, onBack = { screen = "home" })
        else -> HomeScreen(
            onNewProject = {
                vm.reset()
                screen = "form"
            },
            onImport = { screen = "import" },
        )
    }
}
