package com.quickcheck.proxy.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/** Set by MainActivity in onCreate so platform funcs can access the Context. */
@Volatile
internal var androidAppContext: Context? = null

actual val platformName: String = "Android"

actual fun copyToClipboard(text: String): Int {
    val ctx = androidAppContext ?: return 0
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return 0
    cm.setPrimaryClip(ClipData.newPlainText("proxy", text))
    return text.lineSequence().count { it.isNotBlank() }
}

actual fun readFromClipboard(): String {
    val ctx = androidAppContext ?: return ""
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return ""
    return cm.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
}

actual fun openUrl(url: String) {
    val ctx = androidAppContext ?: return
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }
}

actual fun openDeepLink(deepLink: String, fallbackUrl: String) {
    val ctx = androidAppContext ?: return
    val deepIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val ok = runCatching { ctx.startActivity(deepIntent) }.isSuccess
    if (!ok) openUrl(fallbackUrl)
}

actual fun shareText(text: String, subject: String) {
    val ctx = androidAppContext ?: return
    if (text.isBlank()) return
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(intent, subject)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { ctx.startActivity(chooser) }
}

actual fun showToast(message: String) {
    val ctx = androidAppContext ?: return
    Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
}

actual fun setAppLocale(tag: String) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
}
