# MySimon

A modern, feature-rich Android implementation of the classic Simon game, built entirely with **Jetpack Compose** and **Material 3**. This project demonstrates best practices in modern Android development, including type-safe navigation, reactive programming, and persistent local storage.

## 🚀 Features

*   **Classic Gameplay**: Test your memory by replicating increasingly complex sequences of colors and sounds.
*   **Adaptive UI**: Fully optimized for both **Portrait** and **Landscape** orientations, providing a seamless experience on any device.
*   **Persistent History**: Every game session is saved to a local **Room** database, allowing you to review your performance.
*   **Detailed Analytics**: A dedicated Details screen highlights exactly where you made a mistake in previous games using color-coded sequences.
*   **User Personalization**: 
    *   Customizable user profiles with name saving.
    *   Automatic generation of a **unique alphanumeric tag** for future online leaderboard features.
*   **High-Performance Audio**: Low-latency sound feedback using `SoundPool` to ensure audio syncs perfectly with visual highlights.
*   **State Survival**: Robust state management using `SavedStateHandle` to ensure game progress survives process death and configuration changes.
*   **Dark Mode Support**: Dynamic theming that adapts to system-wide dark and light mode settings.

## 🛠 Tech Stack

*   **UI**: Jetpack Compose (Material 3)
*   **Architecture**: MVVM (Model-View-ViewModel) with Repository Pattern
*   **Navigation**: Navigation Compose (Type-safe Navigation)
*   **Database**: Room Persistence Library
*   **Concurrency**: Kotlin Coroutines & Flow
*   **Dependency Management**: Version Catalogs (libs.versions.toml)
*   **Serialization**: Kotlinx Serialization
*   **Audio**: SoundPool API

## 📂 Project Structure

*   `audio`: Manages low-latency sound effects.
*   `data`: Room entities, DAOs, and the Repository coordinating data flow.
*   `model`: Pure business logic for game mechanics (Simon sequence generation).
*   `ui/components`: Reusable Compose components (e.g., FABs).
*   `ui/navigation`: Type-safe route definitions and the central NavHost.
*   `ui/screens`: Screen-specific Composable functions and their respective ViewModels (Home, Game, Detail, Account).
*   `ui/theme`: Application-wide Material 3 styling and color palettes.

## 🛠 Setup & Installation

### Option 1: Install Release Package (Recommended)
1.  Navigate to the [Releases](https://github.com/TommasoRuzzante/VersusSimon_Game/releases) section of this GitHub repository.
2.  Download the latest available `.apk` package.
3.  Transfer the package to your Android device.
4.  Open the file on your device to install it (you may need to enable "Install from Unknown Sources" in your device settings).

### Option 2: Build from Source
1.  Clone the repository.
2.  Open the project in **Android Studio (Ladybug or newer)**.
3.  Ensure you have the latest stable Android SDK installed.
4.  Sync Project with Gradle Files.
5.  Run the `:app` module on an emulator or physical device.

## 🔮 Future Roadmap

*   **Online Leaderboards**: Utilizing the unique user tags to compare scores globally.
*   **Multiplayer Modes**: Head-to-head memory challenges.
*   **iOS Support via KMP**: I will use **Kotlin Multiplatform (KMP)** to provide an iOS version of the application.
*   **App Store Launch**: In the future, the app is going to be available on both the **Google Play Store** and the **iOS App Store**.
