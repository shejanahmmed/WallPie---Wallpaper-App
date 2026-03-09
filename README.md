# WallPie 🎨

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack-Compose-4285F4?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min_SDK-24-orange)](https://developer.android.com/about/dashboards)

WallPie is a premium, high-performance wallpaper application for Android, built with a modern tech stack and focusing on a seamless, visually stunning user experience. It leverages Jetpack Compose for a reactive UI and a robust, scalable architecture.

## 🚀 Key Features

- **Curated Collections**: Browse high-quality wallpapers across diverse categories like Nature, AMOLED, Minimal, and more.
- **Instant Preview**: High-fidelity, full-screen wallpaper previews with real-time UI overlays.
- **Favorites System**: Locally persisted favorites for quick access even when offline.
- **Intelligent Search**: Find the perfect wallpaper with a responsive, real-time search engine.
- **Elegant UI/UX**:
  - Full-screen edge-to-edge experience.
  - Floating navigation bar with transparency.
  - Dynamic theme support (Light, Dark, and System).
  - Smooth animations and transitions.

## 🛠 Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) - Declarative UI for high performance and rapid development.
- **Language**: [Kotlin](https://kotlinlang.org) - Utilizing modern features like Coroutines and Flow for asynchronous operations.
- **Image Loading**: [Glide (with Compose integration)](https://github.com/bumptech/glide) - High-performance image fetching and caching.
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) - Robust SQLite abstraction for managing user favorites.
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) - Type-safe HTTP client for reliable data fetching.
- **Dependency Management**: [Kotlin DSL with Version Catalog](https://developer.android.com/build/migrate-to-catalogs) - Centralized dependency and version handling.
- **Ad Integration**: [Google Mobile Ads (AdMob)](https://developers.google.com/admob) - Integrated for monetization (Configurable).

## 🏗 Architecture

The project follows the **Clean Architecture** principles and **MVVM (Model-View-ViewModel)** pattern, ensuring high maintainability, testability, and scalability.

- **Data Layer**: Handles API communication, database interactions, and repository management.
- **Domain Layer**: Contains business logic and model definitions.
- **UI Layer**: Built entirely with Jetpack Compose components, emphasizing reactivity and modern design patterns.

## 🔧 Getting Started

### Prerequisites

- Android Studio Koala or later.
- Android SDK 24+.
- Internet connectivity (for fetching wallpapers).

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/shejanahmmed/WallPie.git
   ```
2. Open the project in Android Studio.
3. Sync Project with Gradle Files.
4. Run the application on your device or emulator.

## 📂 Project Structure

```text
app/src/main/java/com/shejan/wallpie/
├── data/           # Database, DAOs, and Repositories
├── model/          # Data Models (Wallpaper, AppTheme)
├── network/        # API Interfaces and Retrofit Instances
├── ui/
│   ├── components/ # Reusable UI pieces
│   ├── screens/    # High-level UI screens (Home, Explore, Preview)
│   ├── theme/      # Design System (Colors, Typography, Shapes)
│   └── viewmodel/  # Logic handlers for UI state
└── utils/          # Managers (Preferences, Ads) and Extensions
```

## 🔒 Privacy & Terms

WallPie prioritizes user privacy. For more details, please refer to our [Privacy Policy](https://www.farjan.me/WallPiePrivacyPolicy/).

---

Developed by **Farjan Ahmmed**
