# Grocery Watch

Grocery Watch is an Android app built with Kotlin, Jetpack Compose, Room, and Hilt to help you manage grocery lists, track prices, and compare prices across stores over time.

## Features
- Create, edit, and delete grocery lists
- Add grocery items with categories, notes, and quantities
- Swipe to delete, mark items complete, and view price history
- Track multiple price entries per item with store and date metadata
- View price history charts with optional trend line and predicted next price
- Compare lowest prices across stores with store-based filtering
- Export price history to CSV

## Tech Stack
- Kotlin + Jetpack Compose + Material 3
- MVVM with Hilt dependency injection
- Room database for local persistence
- Navigation Compose for screen flows
- MPAndroidChart embedded in Compose for charts

## Running the app
### Android Studio on a VM (emulator)
1. Install Android Studio Hedgehog (or newer Baklava builds) inside the VM and open the project folder.
2. Install SDK Platform 35 (Android 16/Android 15 preview), Android SDK Build-Tools 35.x, and a Google Play/Google APIs emulator image via **Tools > SDK Manager**. Platform 34 also works, but matching the Android 16.0 Baklava emulator with API 35 removes most resource/linking errors.
3. Create an Android Virtual Device (AVD) with at least 2 GB RAM in **Tools > Device Manager**. If nested virtualization is unavailable, choose the ARM image and disable hardware acceleration; performance will be slower but works in most VMs.
4. Press **Sync Project with Gradle Files**. If the Gradle wrapper JAR is missing, regenerate it with `gradle wrapper --gradle-version 8.6`.
5. Select the bundled **Grocery Watch** run configuration (or choose **Run > Run 'Grocery Watch'**) so Android Studio knows to use the `app` module. If Studio still shows "module not specified", re-import the project from the `build.gradle.kts` in the root and pick the **Grocery Watch** run configuration again.
6. To launch from the command line, run `./gradlew installDebug` then start the AVD via Device Manager.

### Command line (no emulator)
From the project root, build an APK with:
```bash
./gradlew assembleDebug
```
This produces `app/build/outputs/apk/debug/app-debug.apk` for sideloading on a device or emulator.

## Project structure
- `app/src/main/java/com/example/grocerywatch` – application code (DI modules, repository, view model, Compose screens)
- `data` – Room entities and DAOs
- `ui` – Compose screens, navigation, and theming
- `util` – trend calculator and sample data

## Sample data
The Room database pre-populates with three grocery lists, several items, and recent price history entries to demonstrate trends and comparisons on first launch.
