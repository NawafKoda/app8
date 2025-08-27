# 📺 Android TV Video Player App

## 📌 Overview
This is a simple **Android TV application** built with **Android Studio**, designed for smooth remote navigation and video playback.  
It showcases how to build a TV-friendly app using **RecyclerView** for navigation and **Media3 ExoPlayer** for playback.

---

## 🚀 Features
- **TV Launcher integration**
  - Declares TV compatibility with `LEANBACK_LAUNCHER`.
  - Includes a banner (`app_banner.png`) for Android TV Home.
- **Remote navigation**
  - Supports DPAD ↑ ↓ for scrolling through videos.
  - Supports OK/Enter (DPAD_CENTER) for selection.
  - Optional DPAD Left/Right for seeking ±10s during playback.
- **Video list**
  - Videos are listed with **title** + **thumbnail**.
  - Data is stored in `VideoItem` objects.
- **Player**
  - Powered by **Media3 ExoPlayer (1.8.0)**.
  - Shows loading indicator while buffering.
  - Displays video title during playback.
  - Saves playback position (resume).
  - Prompts user whether to **resume from last position** or **start over**.
- **Resume handling**
  - Positions saved in `SharedPreferences` using `ResumeStore`.
  - Clears saved position if user is within last 5s of the video.

---

## 📂 Project Structure
```
app/src/main/java/com/example/app8/
│
├── MainActivity.java      # Displays RecyclerView list of videos
├── VideoAdapter.java      # Binds VideoItem data to RecyclerView (focusable rows)
├── VideoItem.java         # Simple data model for each video
├── PlayerActivity.java    # Plays video using Media3 ExoPlayer with resume support
└── ResumeStore.java       # Utility for saving/loading/clearing playback positions
```

### Layouts (in `res/layout/`)
- `activity_main.xml` → Contains `RecyclerView` for video list.
- `item_video.xml` → Layout for each row (thumbnail + title).
- `activity_player.xml` → Player screen with `PlayerView`, loading bar, and title.

### Resources
- `res/raw/` → Contains bundled videos (`video1.mp4`, `video2.mp4`, `video3.mp4`).
- `res/drawable/` → Thumbnails (`thumb1.png`, `thumb2.png`, `thumb3.png`) + banner (`app_banner.png`).

---

## ⚙️ Key Classes Explained

### `MainActivity.java`
- Builds list of `VideoItem` objects (title, thumbnail, video URI).
- Sets up `RecyclerView` with `VideoAdapter`.
- Requests focus on the first item for TV navigation.

### `VideoAdapter.java`
- Inflates `item_video.xml` rows.
- Ensures each row is focusable and responds to DPAD_CENTER/Enter.
- Launches `PlayerActivity` with selected video URI + title.

### `VideoItem.java`
- Simple model:
  ```java
  public String title;
  public int thumbResId;
  public String uri;
  ```
- Used to pass video data to `VideoAdapter`.

### `PlayerActivity.java`
- Plays selected video with **Media3 ExoPlayer**.
- Shows title and loading spinner.
- Handles playback position resume via `ResumeStore`.
- Prompts user whether to resume or start over.

### `ResumeStore.java`
- Wraps `SharedPreferences` for saving video progress:
  - `save(ctx, key, posMs)`
  - `load(ctx, key)`
  - `clear(ctx, key)`
- Uses URI hash as unique key.

---

## 🛠️ Dependencies
Updated in `app/build.gradle.kts`:
```kotlin
dependencies {
    // AppCompat support library
    implementation("androidx.appcompat:appcompat:1.7.1")

    // Material Design
    implementation("com.google.android.material:material:1.12.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Media3
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
}
```

---

## ▶️ How to Run
1. Clone or open the project in **Android Studio**.
2. Place your video files in `app/src/main/res/raw/` (e.g., `video1.mp4`, `video2.mp4`, `video3.mp4`).
3. Add corresponding thumbnails in `res/drawable/`.
4. Add a banner image `app_banner.png` in `res/drawable-nodpi/` (size 320x180).
5. Run the app on an **Android TV Emulator** or a real TV device.

---

## 🔮 Future Improvements
- Add **subtitle (SRT/VTT) support** using `MediaItem.SubtitleConfiguration`.
- Implement **favorites/watchlist**.
- Load video list dynamically from JSON or a backend API.
- Improve UI with Material Design for TV (Leanback library).
