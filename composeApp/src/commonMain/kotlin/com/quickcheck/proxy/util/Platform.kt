package com.quickcheck.proxy.util

/** Platform identifier for conditional UI tweaks. */
expect val platformName: String

/** Copy text to system clipboard, returns number of non-blank lines copied. */
expect fun copyToClipboard(text: String): Int

/** Read latest text from system clipboard (best effort). */
expect fun readFromClipboard(): String

/** Open external URL in default browser / app. */
expect fun openUrl(url: String)

/** Open a deep link (e.g. tg://) — falls back to https URL if scheme not handled. */
expect fun openDeepLink(deepLink: String, fallbackUrl: String)

/** Share plain text via system share sheet. */
expect fun shareText(text: String, subject: String)

/** Show a transient toast/snackbar message. */
expect fun showToast(message: String)

/** Set application locale at runtime (e.g. "en", "vi", "zh-CN"). */
expect fun setAppLocale(tag: String)
