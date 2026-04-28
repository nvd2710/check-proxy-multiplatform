package com.quickcheck.proxy

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Called from Swift's ContentView via [MainViewControllerKt.MainViewController].
 * The Kotlin/Native compiler generates the class name from the file name:
 * MainViewController.kt → MainViewControllerKt.
 */
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
