# Grocery Watch

Grocery Watch is an Android app built with **Kotlin**, **Jetpack Compose**, **Hilt**, and **Room** to help you manage grocery lists, track prices over time, and compare deals across stores.

## Features
- **Grocery list management** with add/edit/delete, completion toggles, categories, quantities, and notes.
- **Price history** per item with store, price, and date, persisted locally via Room.
- **Price comparison dashboard** to find the lowest price per store with filtering.
- **Charts** showing price trends over time with optional trend lines using MPAndroidChart.
- **CSV import/export** helpers in the repository for portability.
- **Seed data** available through `SampleDataSeeder` for quick previews.

## Architecture
- **MVVM + Repository** backed by Room DAOs (`GroceryListDao`, `GroceryItemDao`, `PriceHistoryDao`, `CategoryDao`).
- **Hilt** provides the database and repository instances (see `DatabaseModule`).
- **Navigation** is handled through a Compose `NavHost` defined in `NavGraph` with overview, detail, and comparison routes.
- UI is composed of dedicated screens and view models for overview, item detail, and price comparison flows.

## Project Structure
- `app/src/main/java/com/motar11/grocerywatch/data` – Room entities, relations, DAOs, database, and repository implementations.
- `app/src/main/java/com/motar11/grocerywatch/ui` – Compose screens, navigation graph, theme, and chart components.
- `app/src/main/java/com/motar11/grocerywatch/util` – Seed data utilities.

## Requirements
- Android Studio Hedgehog (or newer) with **Android Gradle Plugin 8.5** and **JDK 17**.
- Android device or emulator running **API 24+**.

## Build & Run
1. **Sync the project** in Android Studio and let Gradle download dependencies.
2. Use **Run > Run 'app'** to deploy to a connected device/emulator.
3. Or build from the command line:
   ```bash
   ./gradlew assembleDebug
   ```

## Testing
- Run unit tests from Android Studio or via:
  ```bash
  ./gradlew test
  ```
- Run Compose/UI instrumentation tests (if configured) via:
  ```bash
  ./gradlew connectedAndroidTest
  ```

## Notes
- Compose uses the **Material3** design system and the **Compose BOM** defined in `gradle/libs.versions.toml`.
- Default app theme is configured in `Theme.kt`; update colors/typography there.
- If you enable proguard/R8, adjust rules in `proguard-rules.pro` as needed.
