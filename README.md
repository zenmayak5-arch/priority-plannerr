# FocusPriority: 3-Tier Priority Android Task Planner 🚀

A modern, offline-first Native Android task planner built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Room SQLite Database**.

FocusPriority implements the **Traffic Light / Energy Priority System** to eliminate task overwhelm and give you laser focus each day.

---

## 🎨 The 3-Tier Priority Architecture

| Priority | Color | Role & Philosophy |
| :--- | :--- | :--- |
| **Priority 1** | 🔴 **Crimson Red** | **Must-Do / Non-Negotiable** — 1 to 3 critical tasks that define a successful day. Never skip these. |
| **Priority 2** | 🟡 **Amber Yellow** | **Important & High-Value** — Secondary tasks that significantly advance goals once P1 is tackled. |
| **Priority 3** | 🟢 / 🔵 / 🟣 / 🩵 / 🌸 **Customizable Color** | **Today's Routine & Chores** — Everyday responsibilities, errands, and habits for today. You can pick **Emerald Green, Sapphire Blue, Amethyst Purple, Ocean Teal, or Rose Pink** in Settings! |

---

## ✨ Key Features

1. **Traffic Light Priority System**:
   - Color-coded badges, visual glow cards, and progress metrics for each priority level.
   - Filter chips to view All, Red-only, Yellow-only, or Priority 3 tasks.
2. **Offline-First Local SQLite Database (Room)**:
   - Zero login required.
   - Immediate loading and 100% private.
   - Reactive updates using Kotlin Coroutines `Flow`.
3. **Daily Automatic Rollover**:
   - Intelligently detects unfinished tasks from previous days when you start your day.
   - Provides a 1-tap "Move All to Today" banner, or lets you roll over individual tasks.
4. **Subtasks & Interactive Checklists**:
   - Add mini checklist steps inside any task.
   - Visual progress bars showing progress (e.g. `2 of 3 subtasks completed`).
5. **Exact Scheduled Alarms & Push Notifications**:
   - Set due times with an interactive `TimePicker`.
   - Exact alarms using Android `AlarmManager`.
   - High-importance notification channel with an interactive **"Mark as Done"** action button right in the notification shade.
   - `BootReceiver` automatically reschedules pending alarms on device restart.
6. **Live Theme & Color Customizer**:
   - Customize the 3rd tier color dynamically in **Settings**.
   - Dark Mode, Light Mode, or System Default theme support.

---

## 📂 Project Structure

```
priority-planner/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── res/
│       │   ├── values/ (strings, colors, themes)
│       │   ├── drawable/ (vector launcher icons)
│       │   └── mipmap-anydpi-v26/ (adaptive icons)
│       └── java/com/priorityplanner/app/
│           ├── PriorityPlannerApp.kt          # Application class & Notification Channels
│           ├── MainActivity.kt                # Jetpack Compose Entry & Permission Handling
│           ├── data/
│           │   ├── model/
│           │   │   ├── Priority.kt            # Priority enums & 3rd-color palettes
│           │   │   ├── TaskEntity.kt          # Room entity for tasks
│           │   │   ├── SubTaskEntity.kt       # Room entity for checklist items
│           │   │   └── TaskWithSubTasks.kt    # Relation model
│           │   ├── local/
│           │   │   ├── TaskDao.kt             # Room DAO with Flow queries
│           │   │   └── AppDatabase.kt         # Room SQLite instance
│           │   ├── preferences/
│           │   │   └── PreferenceManager.kt   # DataStore preferences (theme & color)
│           │   └── repository/
│           │       └── TaskRepository.kt      # Repository abstraction layer
│           ├── notification/
│           │   ├── NotificationHelper.kt      # AlarmManager & NotificationCompat
│           │   ├── ReminderReceiver.kt        # BroadcastReceiver for alarms & mark-done
│           │   └── BootReceiver.kt            # Re-registers alarms on reboot
│           └── ui/
│               ├── theme/                     # Material 3 colors, dynamic priority colors & typography
│               ├── viewmodel/                 # TaskViewModel & TaskViewModelFactory
│               ├── components/
│               │   ├── PriorityBadges.kt      # Badges & Priority Selection Cards
│               │   ├── TaskCard.kt            # Rich card with subtasks & 3-dots menu
│               │   ├── DailyStatsHeader.kt    # Daily progress bar & P1/P2/P3 pills
│               │   ├── RolloverBanner.kt      # Unfinished tasks rollover banner
│               │   └── AddEditTaskSheet.kt    # Modal bottom sheet to create/edit tasks
│               └── screens/
│                   ├── HomeScreen.kt          # Main planner view with date navigation
│                   ├── CompletedHistoryScreen.kt # Finished tasks archive
│                   ├── SettingsScreen.kt      # 3rd-color picker & theme selector
│                   └── MainScreen.kt          # Scaffold with Bottom Navigation
├── gradle/
│   ├── libs.versions.toml                     # Modern Gradle Version Catalog
│   └── wrapper/gradle-wrapper.properties
├── build.gradle.kts                           # Root Gradle script
├── settings.gradle.kts                        # Gradle settings
└── gradlew / gradlew.bat                      # Gradle wrapper scripts
```

---

## 🚀 How to Run the App

### Option A: Open with Android Studio (Recommended)

1. Launch **Android Studio**.
2. Click **Open** (or `File > Open...`).
3. Navigate to:
   ```
   C:\Users\mayan\.gemini\antigravity\scratch\priority-planner
   ```
4. Click **OK**. Android Studio will automatically sync the Gradle dependencies and configure the project.
5. Select an Emulator (or connect your physical Android phone via USB with USB Debugging enabled).
6. Press the green **▶ Run** button (or `Shift + F10`).

### Option B: Build APK via Command Line

To build the debug APK:
```powershell
cd C:\Users\mayan\.gemini\antigravity\scratch\priority-planner
.\gradlew.bat assembleDebug
```
The resulting `.apk` file will be generated at:
```
app\build\outputs\apk\debug\app-debug.apk
```
You can install it directly onto your phone:
```powershell
adb install app\build\outputs\apk\debug\app-debug.apk
```

---

## 📱 Permissions Used

- `android.permission.POST_NOTIFICATIONS`: Android 13+ runtime permission to trigger task reminder alerts.
- `android.permission.SCHEDULE_EXACT_ALARM`: Ensures task alarms ring precisely at the scheduled time.
- `android.permission.RECEIVE_BOOT_COMPLETED`: Automatically restores pending alarms when the phone restarts.
- `android.permission.VIBRATE`: Provides haptic feedback for reminder notifications.
