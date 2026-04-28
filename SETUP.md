# Check Proxy — Multiplatform Setup

Compose Multiplatform project targeting Android + iOS, sharing ~80% Kotlin code.
Built from the Android-only project at `../android/`.

## Status

- [x] Skeleton project structure
- [x] Pure Kotlin code ported (parser, formatter, viewmodel, theme)
- [x] Ktor-based proxy checker (replaces OkHttp)
- [x] Platform abstractions (clipboard, share, urls, locale) for Android + iOS
- [x] Android entry (MainActivity + Manifest + network_security_config)
- [x] iOS entry (iOSApp.swift + ContentView.swift + Info.plist + Compose UIViewController bridge)
- [x] GitHub Actions workflow for unsigned IPA build
- [ ] **Xcode project file** (`iosApp.xcodeproj/project.pbxproj`) — see "Generate Xcode project" below
- [ ] **Full UI port** — currently App.kt is a stub. Port ConfigCard / InputCard / ProgressCard / ResultsBlock / GetProxiesScreen / MainMenu from `../android/app/src/main/java/com/quickcheck/proxy/ui/ProxyCheckerScreen.kt` into `composeApp/src/commonMain/kotlin/com/quickcheck/proxy/ui/`
- [ ] iOS-specific UI tweaks (toast → snackbar, file picker → UIDocumentPicker)
- [ ] App icon for both platforms

---

## 1. Generate Xcode project (.xcodeproj)

The `.pbxproj` file is too complex/binary-like to hand-write reliably. Two options:

### Option A — JetBrains Compose Multiplatform Wizard (RECOMMENDED)

1. Open https://kmp.jetbrains.com/
2. Fill: project name `CheckProxy`, package `com.quickcheck.proxy`, Android + iOS only
3. Download generated zip
4. Copy ONLY the `iosApp/iosApp.xcodeproj/` folder into our project at `iosApp/iosApp.xcodeproj/`
5. Open `iosApp/iosApp.xcodeproj` in Xcode (on a Mac), make sure:
   - Bundle ID = `com.quickcheck.proxy`
   - Deployment target = iOS 14.0
   - Framework Search Paths point to `../composeApp/build/bin/iosArm64/releaseFramework/`
6. Verify it builds locally on Mac (or just push to GitHub and let CI build)

### Option B — Use Android Studio's Kotlin Multiplatform plugin

1. Install plugin "Kotlin Multiplatform" in Android Studio
2. File ▸ New Project ▸ Kotlin Multiplatform App
3. Same settings as Option A
4. Copy generated `iosApp/` folder over

---

## 2. Push to GitHub

```bash
cd D:\SOURCE\quickcheck\multiplatform
git init
git add .
git commit -m "Initial multiplatform skeleton"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/check-proxy-multiplatform.git
git push -u origin main
```

GitHub Actions will auto-trigger on push.
**Repo can be public or private** — public has free 2000 macOS minutes/month.
Private repos pay $0.08/min macOS time after free tier.

---

## 3. Wait for CI build

1. Go to GitHub repo ▸ Actions tab
2. Watch "Build iOS IPA (unsigned)" workflow
3. Takes ~10-15 minutes first time (cold cache)
4. When done, scroll to "Artifacts" section at bottom of run summary
5. Download `CheckProxy-ipa.zip`
6. Unzip → get `CheckProxy-unsigned.ipa`

---

## 4. Sign with eSign and install

1. Upload `CheckProxy-unsigned.ipa` to your eSign service (most have a web/app interface)
2. eSign signs with your distribution certificate
3. Download signed IPA → install on iPhone via eSign install link
4. Trust the certificate in Settings ▸ General ▸ VPN & Device Management

---

## 5. Iterate

When you change Kotlin code:

1. Commit + push to GitHub
2. CI rebuilds IPA automatically
3. Re-sign + reinstall

---

## Local development on Windows

You CAN edit and test the **Android** target on Windows with Android Studio:

1. Open `D:\SOURCE\quickcheck\multiplatform\` in Android Studio
2. Sync Gradle
3. Run `composeApp` configuration on emulator/device
4. Verify Compose UI works

For iOS, you cannot run locally on Windows. Only build via GitHub Actions
or use a Mac (yours, borrowed, or cloud).

---

## Known limitations / TODOs

| Item | Severity | Note |
|---|---|---|
| iOS proxy connection | HIGH | Ktor Darwin engine has limited proxy auth support. May need custom NSURLSessionConfiguration with `connectionProxyDictionary`, or fall back to a SOCKS5 library. Test first to see how `ip-api.com` test fares through HTTP proxies on iOS. |
| iOS SOCKS5 | MEDIUM | iOS NSURLSession does not natively support SOCKS5 with auth. Consider a separate library or skipping SOCKS5 on iOS. |
| iOS toast | LOW | Need a Compose-level Snackbar/SnackbarHost since iOS has no native toast. |
| iOS file picker for import | MEDIUM | Need to wire UIDocumentPicker via expect/actual for `Import from file` menu item. |
| iOS file save for export | MEDIUM | Need UIDocumentPicker.exportToService or share sheet. |
| Locale switching at runtime | MEDIUM | iOS requires app restart after changing AppleLanguages. Consider a confirmation dialog. |
| App icon iOS | LOW | Generate AppIcon.appiconset with 1024x1024 master + various sizes via icon kitchen / Xcode. |
| RTL (Arabic) on iOS | LOW | Compose Multiplatform handles via `LayoutDirection.Rtl`. |
