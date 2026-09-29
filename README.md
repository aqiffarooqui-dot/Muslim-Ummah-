# Muslim Pro (Android)

Comprehensive Islamic companion application built with **Jetpack Compose**, **Kotlin**, **Room Database**, and **Material Design 3**.

---

## Features

- **Prayer Times & Countdown**: Astronomical calculation (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha) with live second-by-second countdown. Supports Muslim World League, ISNA, Umm Al-Qura, Egyptian, and Karachi calculation methods with Shafi/Hanafi Asr juristic options.
- **Qibla Compass**: Great-circle trigonometry direction to Kaaba in Makkah with real-time sensor rotation and haptic alignment feedback.
- **Holy Quran**: 114 Surahs with Arabic Uthmani text, phonetics, English translation, bookmarking, and audio recitation streaming by Sheikh Mishary Rashid Alafasy.
- **Digital Tasbih**: Tactile circular tap counter with vibration haptics, presets (*SubhanAllah*, *Alhamdulillah*, *Allahu Akbar*, etc.), lap tracking, and persistent Room log history.
- **Hisn al-Muslim (Duas & Azkar)**: Categorized daily supplications with Arabic, transliteration, English meaning, and references.
- **99 Names of Allah**: Asma'ul Husna with meanings, transliterations, and spiritual reflection notes.
- **Islamic Calendar & Fasting**: Hijri date converter, Islamic holidays timeline, and Sunnah fasting guide.
- **Google Sign-In & Super Admin Console**:
  - Super Admin role for **`aqiffarooqui@gmail.com`** with permanent VIP unlock.
  - Admin management dashboard to track user counts (total, free, premium active), view MRR, and grant/modify subscriptions.
- **Premium Subscription Features**:
  - 100% Ad-Free interface
  - 5 Elite Quran Reciters (Mishary, Abdul Basit, As-Sudais, Ash-Shuraim, Al-Ghamdi)
  - Historic Adhan Voice Alerts (Makkah, Madinah, Al-Aqsa, Cairo)
  - Ramadan & Qada Prayer Tracker (Suhoor, Iftar, missed prayer tallies)
  - Audio Ruqyah and customized dhikr routines

---

## Android Build & CI/CD Setup

### 1. Gradle Wrapper
The repository includes the complete Gradle Wrapper files:
- `gradlew` (Linux / macOS execution script)
- `gradlew.bat` (Windows script)
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`

To build the debug APK locally:
```bash
./gradlew assembleDebug
```
To run unit and Robolectric tests:
```bash
./gradlew testDebugUnitTest
```

---

### 2. Debug Keystore Setup
The project configuration in `app/build.gradle.kts` expects `debug.keystore` in the root project directory:
```kotlin
create("debugConfig") {
  storeFile = file("${rootDir}/debug.keystore")
  storePassword = "android"
  keyAlias = "androiddebugkey"
  keyPassword = "android"
}
```

- `debug.keystore.base64` is committed in the repository so any environment or CI runner can restore the keystore:
  ```bash
  base64 -d debug.keystore.base64 > debug.keystore
  ```
- The included GitHub Actions workflow automatically restores this file before compiling.

---

### 3. GitHub Actions Automated Builds
The CI workflow is configured in `.github/workflows/android.yml`:
- **Triggers**: On every `push` to `main`/`master`, `pull_request`, or manual `workflow_dispatch`.
- **Steps**:
  1. Sets up JDK 17 (Temurin).
  2. Restores `debug.keystore` from base64.
  3. Executes unit tests (`./gradlew testDebugUnitTest`).
  4. Compiles the debug APK (`./gradlew assembleDebug`).
  5. Uploads the debug APK as a downloadable GitHub Action Artifact (`MuslimPro-Debug-APK`).

---

### 4. Release Signing Configuration (Optional)
When you are ready to produce a signed release APK or Google Play bundle:
1. Generate an upload keystore:
   ```bash
   keytool -genkey -v -keystore my-upload-key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Convert the keystore to base64:
   ```bash
   base64 -w 0 my-upload-key.jks > upload-key.base64
   ```
3. Add the following secrets in your GitHub repository (**Settings > Secrets and variables > Actions > New repository secret**):
   - `KEYSTORE_BASE64`: The contents of `upload-key.base64`
   - `STORE_PASSWORD`: Keystore password
   - `KEY_ALIAS`: `upload`
   - `KEY_PASSWORD`: Key password

Once these secrets are added, the GitHub Actions workflow will automatically build, sign, and upload the `MuslimPro-Release-APK` artifact on every push.
