package com.quickcheck.proxy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickcheck.proxy.ui.MainViewModel
import com.quickcheck.proxy.ui.theme.AppTheme

/**
 * Root entry point shared between Android and iOS.
 *
 * NOTE — this is a STUB UI. The full Check Proxy UI from the Android-only
 * project (ConfigCard, InputCard, ProgressCard, ResultsBlock, etc.) needs
 * to be ported into commonMain by:
 *   1. Replacing Toast.makeText / Intent calls with [com.quickcheck.proxy.util.shareText],
 *      openUrl, copyToClipboard etc.
 *   2. Replacing SharedPreferences with multiplatform-settings.
 *   3. Replacing rememberLauncherForActivityResult (file pickers) with platform-
 *      specific composables wrapped via expect/actual.
 *
 * For the first iOS build, this stub just shows that Compose UI renders on iOS.
 */
@Composable
fun App() {
    AppTheme {
        val vm = remember { MainViewModel() }
        val s by vm.state.collectAsState()

        Scaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Check Proxy — multiplatform skeleton OK\n" +
                        "Input length: ${s.input.length}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
