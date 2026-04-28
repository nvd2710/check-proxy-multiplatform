package com.quickcheck.proxy.util

import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIPasteboard
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual val platformName: String = "iOS"

actual fun copyToClipboard(text: String): Int {
    UIPasteboard.generalPasteboard.string = text
    return text.lineSequence().count { it.isNotBlank() }
}

actual fun readFromClipboard(): String {
    return UIPasteboard.generalPasteboard.string ?: ""
}

actual fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url) ?: return
    dispatch_async(dispatch_get_main_queue()) {
        UIApplication.sharedApplication.openURL(
            url = nsUrl,
            options = emptyMap<Any?, Any>(),
            completionHandler = null,
        )
    }
}

actual fun openDeepLink(deepLink: String, fallbackUrl: String) {
    val deep = NSURL.URLWithString(deepLink)
    if (deep != null && UIApplication.sharedApplication.canOpenURL(deep)) {
        openUrl(deepLink)
    } else {
        openUrl(fallbackUrl)
    }
}

actual fun shareText(text: String, subject: String) {
    if (text.isBlank()) return
    dispatch_async(dispatch_get_main_queue()) {
        val activity = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null,
        )
        val root = UIApplication.sharedApplication
            .keyWindow?.rootViewController
        root?.presentViewController(activity, animated = true, completion = null)
    }
}

actual fun showToast(message: String) {
    // iOS has no native toast; the Compose UI layer should render its own snackbar.
    // Stub here — real impl will be in Compose layer via a ToastHost composable.
    println("[Toast] $message")
}

actual fun setAppLocale(tag: String) {
    // iOS per-app language is set via Info.plist (CFBundleAllowMixedLocalizations)
    // and AppleLanguages NSUserDefaults entry. Requires app restart.
    // Stub for now — to be implemented when localization is wired.
    val defaults = platform.Foundation.NSUserDefaults.standardUserDefaults
    defaults.setObject(listOf(tag), forKey = "AppleLanguages")
    defaults.synchronize()
}
