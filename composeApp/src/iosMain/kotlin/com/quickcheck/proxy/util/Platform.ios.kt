@file:Suppress("DEPRECATION") // openURL(NSURL) deprecated in iOS 10 but works simpler

package com.quickcheck.proxy.util

import platform.Foundation.NSURL
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard

actual val platformName: String = "iOS"

actual fun copyToClipboard(text: String): Int {
    UIPasteboard.generalPasteboard.string = text
    return text.lineSequence().count { it.isNotBlank() }
}

actual fun readFromClipboard(): String =
    UIPasteboard.generalPasteboard.string ?: ""

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}

actual fun openDeepLink(deepLink: String, fallbackUrl: String) {
    val deep = NSURL.URLWithString(deepLink)
    val app = UIApplication.sharedApplication
    if (deep != null && app.canOpenURL(deep)) {
        app.openURL(deep)
    } else {
        openUrl(fallbackUrl)
    }
}

actual fun shareText(text: String, subject: String) {
    // TODO: Wire UIActivityViewController via root view controller once
    // full Compose UI is ported. Stub for skeleton compile.
    if (text.isBlank()) return
    println("[shareText] subject=$subject, ${text.length} chars")
}

actual fun showToast(message: String) {
    // iOS has no native toast. Compose UI layer should render its own
    // SnackbarHost. For skeleton, just log.
    println("[Toast] $message")
}

actual fun setAppLocale(tag: String) {
    val defaults = NSUserDefaults.standardUserDefaults
    defaults.setObject(listOf(tag), forKey = "AppleLanguages")
    defaults.synchronize()
    // iOS requires app restart to apply locale change. Will add restart UX later.
}
