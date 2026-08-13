<div align="center">

# 🌱 MIMO

**A mindful app closer for Android — set a timer, and MIMO closes the app for you.**

[![Build](https://github.com/ShTanzir/Mimo/actions/workflows/build.yml/badge.svg)](https://github.com/ShTanzir/Mimo/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/ShTanzir/Mimo?color=4E9C74)](https://github.com/ShTanzir/Mimo/releases/latest)
[![License](https://img.shields.io/github/license/ShTanzir/Mimo?color=7FCB9E)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/minSdk-26-B7E4C7)](https://developer.android.com/about/versions/oreo)
[![Platform](https://img.shields.io/badge/platform-Android-4E9C74?logo=android&logoColor=white)](https://www.android.com)

[Download](../../releases/latest) · [Wiki](../../wiki) · [Report a bug](../../issues) · [Request a feature](../../issues)

</div>

---

## What is MIMO?

Most screen-time apps just show you a chart. **MIMO does something instead of just telling you something.**

Pick the apps that pull you in, set a delay — immediate, 10 seconds, 1 minute, 5 minutes, or a custom time — and once that time is up, MIMO gently closes the app for you. No lectures, no shame, just a quiet nudge back to whatever you meant to be doing.

<div align="center">
<img src="assets/Mimo.png" width="120" alt="MIMO app icon" />
</div>

---

## ✨ Features

<table>
<tr>
<td width="50%" valign="top">

### Core
- Guard any installed app with a custom delay
- Presets: Immediate · 10s · 30s · 1min · 5min · 15min · Custom
- Per-app enable/disable, plus a master on/off switch
- Toggleable search, "guarded only" filter

### Warnings & Interventions
- Full-screen countdown warning in the final seconds
- Live countdown notification with progress
- One-time daily **Snooze +1 min** per app
- Progressive haptic feedback as time runs out

### Focus Session
- Pomodoro-style distraction-free blocks (15 / 25 / 45 / 60 min)
- Closes *any* app instantly — except calls & texts

</td>
<td width="50%" valign="top">

### Insight
- Daily closures + total guarded time
- Most-guarded apps leaderboard
- Full closure history log

### Privacy & Control
- 4-digit PIN protection for settings
- Fully offline — zero network permissions
- Export / import rules as JSON

### Design
- Light-green, glassmorphism-inspired Material 3 UI
- Dark / light theme
- Smooth transitions throughout

</td>
</tr>
</table>

📖 Full feature breakdown: see the [Wiki → Features](../../wiki/Features).

---

## 📲 Install

Grab the latest APK from **[Releases](../../releases/latest)**, enable "install from unknown sources," and open it. MIMO will walk you through the required permissions on first launch (and will keep asking until they're granted — it can't work without them).

| Permission | Why |
|---|---|
| Accessibility Service | Detects which app is in the foreground |
| Display over other apps | Shows the full-screen countdown warning |
| Notifications | Live countdown + closure alerts |
| Ignore battery optimization | Keeps the guard running reliably |

None of these let MIMO read screen content — only the package name of whatever app is currently open. See [Permissions & Privacy](../../wiki/Permissions-and-Privacy) for details.

---

## 🛠️ Build from source

```bash
git clone https://github.com/ShTanzir/Mimo.git
cd REPO
```

**Android Studio:** open the folder, let Gradle sync, hit Run.

**Command line:**
```bash
chmod +x gradlew
./gradlew assembleDebug
```
The APK lands in `app/build/outputs/apk/debug/`.

**CI:** every push triggers [`.github/workflows/build.yml`](.github/workflows/build.yml), which builds debug + unsigned release APKs and (on a `v*` tag) publishes a GitHub Release automatically.

Requirements: JDK 17, Android SDK (compileSdk 34), Kotlin 1.9.24.

📖 More detail: [Wiki → Build from Source](../../wiki/Build-from-Source).

### Custom app icon

Drop a square PNG at `assets/Mimo.png` and push — CI generates every launcher density automatically (`scripts/generate_icons.sh`). No file there yet? The build falls back to a bundled default icon, so it never breaks.

---

## 🏗️ Architecture

```
com.mimo.app
├── data/            Room entities + DAOs + database
├── repository/       MimoRepository — single source of truth
├── service/          AccessibilityService, foreground countdown service,
│                      notifications, boot/snooze receivers, overlay warning
├── util/              Permissions, timing, focus session, backup/export
└── ui/                Jetpack Compose screens (MVVM, one package per feature)
    ├── onboarding, permissions, applist, appdetail
    ├── stats, settings, about, focus, lock
    └── theme, navigation, components
```

**Stack:** Kotlin · Jetpack Compose · Material 3 · Room · DataStore · WorkManager · Coroutines

**How closing actually works:** Android doesn't let a non-root app force-kill another app. MIMO uses an `AccessibilityService` to detect the foreground package, and — once your timer runs out — calls `performGlobalAction(GLOBAL_ACTION_HOME)`, sending you home. It's Play Store-safe and needs no root. Full write-up: [Wiki → How It Works](../../wiki/How-It-Works).

---

## 🔒 Privacy

MIMO is offline by design.

- No internet permission — nothing ever leaves your device
- No screen content, text, or personal data is read from other apps — only the foreground package name
- All rules and history live in a local Room database
- No analytics, no crash reporting, no ads

---

## 🗺️ Roadmap

Scheduled rules, app categories, usage graphs, a home screen widget, and more are tracked in the [Wiki → Roadmap](../../wiki/Roadmap). Ideas welcome — [open an issue](../../issues).

---

## 🤝 Contributing

Issues and pull requests are welcome. Please open an issue first for anything non-trivial so we can talk it through.

---

## 📄 License

Released under the [MIT License](LICENSE).

<div align="center">

Built with care for calmer screen time. 🌿

</div>
