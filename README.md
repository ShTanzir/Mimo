# MIMO — Mindful App Closer

**Package:** `com.mimo.app`
**Version:** 1.2.0
**Language:** Kotlin (Jetpack Compose, Material 3)
**Min SDK:** 26 · **Target SDK:** 34

MIMO লেটস ইউজার সিলেক্ট করা অ্যাপগুলো একটা নির্দিষ্ট সময় পর নিজে থেকেই বন্ধ (হোম স্ক্রিনে নিয়ে যায়) করে দেয় — যাতে ইমপালসিভ স্ক্রল/ইউজেজ কমানো যায়। যেমন Instagram-এ 1 মিনিট সেট করলে, ঐ অ্যাপ ১ মিনিট পর MIMO নিজে থেকে বন্ধ করে দিবে।

## কীভাবে কাজ করে (How it works)

1. অ্যাপ ওপেন করলে ইউজার তার ফোনের সব লঞ্চেবল অ্যাপের লিস্ট দেখতে পাবে।
2. যেকোনো অ্যাপে ট্যাপ করলে delay সেট করা যাবে: **Immediate / 10s / 30s / 1min / 5min / 15min / Custom**।
3. একবার "Guard" করলে, MIMO-এর **AccessibilityService** ব্যাকগ্রাউন্ডে চলবে এবং ঐ অ্যাপ ফোরগ্রাউন্ডে এলেই টাইমার শুরু হবে।
4. সময় শেষ হলে MIMO `GLOBAL_ACTION_HOME` পারফর্ম করে অ্যাপটাকে ব্যাকগ্রাউন্ডে পাঠিয়ে দেয়, আর একটা নোটিফিকেশন দেখায়।
5. প্রতিটা closure Room database-এ লগ হয়, যা Stats স্ক্রিনে দেখা যায়।

> Android কোনো থার্ড-পার্টি অ্যাপকে root ছাড়া সরাসরি অন্য একটা অ্যাপ "kill" করতে দেয় না। তাই MIMO ব্যবহারকারীকে হোম স্ক্রিনে নিয়ে গিয়ে কার্যকরভাবে অ্যাপ বন্ধ করে — এটাই প্লে-স্টোর-সেফ, non-root approach.

## ২০+ ফিচার

1. সব launchable app-এর তালিকা (আইকনসহ)
2. Search / filter apps
3. "Guarded only" ফিল্টার টগল
4. System apps দেখানোর অপশন
5. প্রিসেট টাইমার: Immediate, 10s, 30s, 1min, 5min, 15min
6. Custom টাইমার (মিনিট + সেকেন্ড)
7. প্রতি অ্যাপের জন্য আলাদা enable/disable টগল
8. Master on/off সুইচ (পুরো MIMO সাময়িক বন্ধ রাখা)
9. Live countdown notification (progress bar-সহ)
10. Closure notification ("MIMO closed X")
11. Room database দিয়ে persistent rules storage
12. Closure history log (recent 200 entries)
13. আজকের statistics: কতবার বন্ধ হয়েছে, কত সময় guarded ছিল
14. Most-guarded apps leaderboard
15. Clear history অপশন
16. Custom close message প্রতি অ্যাপে
17. One-time snooze preference per app (data-layer ready)
18. Vibrate-on-warning preference per app
19. Onboarding flow (3-page swipeable intro)
20. Permissions checklist screen (Accessibility, Overlay, Notifications, Battery optimization)
21. Dark/Light theme টগল, persisted with DataStore
22. Optional 4-digit PIN lock for settings
23. Boot receiver — রিবুটের পর accessibility বন্ধ থাকলে reminder notification
24. Glassmorphism, nature-inspired Material 3 UI (sage/moss/clay palette)
25. Typography-based adaptive app icon ("M" monogram, vector — no external assets needed)
26. MVVM architecture (ViewModel + StateFlow + Repository pattern)
27. Fully offline — no network permissions, no data leaves the device

## প্রজেক্ট স্ট্রাকচার

```
MIMO/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── kotlin/com/mimo/app/
│   │   │   ├── MainActivity.kt, MimoApplication.kt
│   │   │   ├── data/            # Room entities + DAOs + Database
│   │   │   ├── repository/      # MimoRepository
│   │   │   ├── service/         # AccessibilityService, CountdownForegroundService, Notifications, BootReceiver
│   │   │   ├── util/            # Prefs (DataStore), PermissionUtils, TimeUtils, AppInfoProvider
│   │   │   └── ui/              # Compose screens per feature (onboarding, permissions, applist, appdetail, stats, settings, lock)
│   │   └── res/                 # strings, colors, themes, adaptive icon vectors
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew / gradlew.bat / gradle/wrapper/
└── .github/workflows/build.yml   # CI: builds debug + unsigned release APK
```

## GitHub Actions দিয়ে বিল্ড করা (যেভাবে করবেন)

1. এই zip ফাইলটা extract করুন।
2. একটা নতুন GitHub repository বানান (public বা private, দুটোই চলবে)।
3. Extract করা সব ফাইল সেই repo-তে push করুন:
   ```bash
   cd MIMO
   git init
   git add .
   git commit -m "Initial MIMO commit"
   git branch -M main
   git remote add origin https://github.com/<your-username>/<repo-name>.git
   git push -u origin main
   ```
4. GitHub-এ repo-র **Actions** ট্যাবে যান — "Build MIMO APK" workflow অটোমেটিক রান হবে (push করার সাথে সাথেই)। চাইলে "Run workflow" বাটন দিয়ে ম্যানুয়ালিও চালাতে পারবেন।
5. Workflow শেষ হলে সেই run-এর নিচে **Artifacts** সেকশনে `mimo-debug-apk` এবং `mimo-release-apk-unsigned` পাবেন — download করে ফোনে ইনস্টল করুন (release APK unsigned, তাই ইনস্টলের আগে signing লাগবে যদি production-এ দিতে চান; debug APK সরাসরি ইনস্টল করা যায়)।

## Signing একটা রিলিজ APK (ঐচ্ছিক)

Play Store বা production release-এর জন্য চাইলে GitHub Actions-এ একটা signing step যোগ করতে হবে (keystore secrets ব্যবহার করে)। ডিফল্টভাবে workflow-টা শুধু unsigned build দেয়, যা টেস্টিং এর জন্য যথেষ্ট (debug build ইনস্টল করেই টেস্ট করা যাবে)।

## গুরুত্বপূর্ণ পারমিশন

- **Accessibility Service** — কোন অ্যাপ foreground-এ এসেছে সেটা বুঝতে (কোনো screen content/text পড়ে না)।
- **Display over other apps** — countdown warning দেখানোর জন্য (ভবিষ্যতে overlay UI যোগ করলে দরকার হবে)।
- **Notifications** — countdown ও closure notification দেখাতে।
- **Ignore battery optimizations** — background-এ reliably চলার জন্য।

সবকিছু local; কোনো ইন্টারনেট পারমিশন নেই, কোনো ডেটা বাইরে যায় না।

## 🆕 v1.2.0-এ যা যোগ হয়েছে

- 🖼️ **Custom app icon** — `assets/Mimo.png` রাখলেই CI build-এ সেটা থেকে সব density-র launcher icon অটোমেটিক জেনারেট হয় (`scripts/generate_icons.sh`, ImageMagick দিয়ে)। না রাখলে একটা ডিফল্ট light-green icon ব্যবহার হবে, বিল্ড কখনো ভাঙবে না।
- 🔐 **Persistent permission gate** — critical permission (Accessibility, Overlay, Notification) কোনো একটা মিসিং থাকলে অ্যাপ প্রতিবার ওপেন/resume হওয়ার সময় Permissions screen-ই দেখাবে, প্রতিটা পারমিশনের পাশে "How?" ট্যাপ করলে ধাপে ধাপে instructions দেখাবে।
- 🎨 **নতুন হালকা সবুজ (light green) + glass UI** — পুরো থিম রিডিজাইন করা হয়েছে, GlassCard-এ এখন হেয়ারলাইন বর্ডার + shadow যোগ হয়েছে।
- 🔍 **Search icon toggle** — App List-এ এখন টগলযোগ্য সার্চ বার (আগে সবসময় দেখাত)
- 📄 **About page** — অ্যাপ ভার্সন, প্রাইভেসি স্টেটমেন্ট, ব্যবহৃত টেক স্ট্যাক দেখা যাবে (Settings → About MIMO)
- 🖥️ **Full-screen countdown warning** — শেষ ৫ সেকেন্ডে (বা delay-এর ৪০%, যেটা ছোট) পুরো স্ক্রিনে একটা warning overlay দেখাবে
- ⏱️ **Notification + overlay থেকে Snooze (+1 min)** — প্রতি অ্যাপে একবার করে ব্যবহারযোগ্য
- 📳 **Progressive haptic feedback** — শেষের দিকে vibration আরও ঘন ও তীব্র হয়
- 💾 **Export/Import rules (JSON)** — Settings → Backup & restore থেকে
- ⚡ **Focus Session (Pomodoro mode)** — App List-এর top bar থেকে চালু করলে, ফোন কল/SMS বাদে সব অ্যাপ সাথে সাথে বন্ধ হয়ে যাবে নির্দিষ্ট সময় ধরে
- ✨ স্মুথ অ্যানিমেশন সব স্ক্রিন জুড়ে (fade/expand transitions)

