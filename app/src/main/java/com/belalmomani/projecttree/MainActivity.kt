package com.belalmomani.projecttree

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.belalmomani.projecttree.form.FormViewModel
import com.belalmomani.projecttree.ui.FormScreen
import com.belalmomani.projecttree.ui.HomeScreen
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
    val context = LocalContext.current
    var screen by rememberSaveable { mutableStateOf("home") }

    BackHandler(enabled = screen != "home") { screen = "home" }

    when (screen) {
        "form" -> FormScreen(
            vm = vm,
            onBack = { screen = "home" },
            onContinue = {
                Toast.makeText(context, "توليد البرومبت في المرحلة التالية", Toast.LENGTH_SHORT).show()
            },
        )
        else -> HomeScreen(onNewProject = {
            vm.reset()
            screen = "form"
        })
    }
}
