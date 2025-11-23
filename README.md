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
1. Open the project in Android Studio Hedgehog or newer.
2. Ensure the Gradle wrapper jar is available. If it is missing, regenerate it with `gradle wrapper --gradle-version 8.6`.
3. Use **Run > Run 'app'** or execute `./gradlew assembleDebug` from the project root.

## Project structure
- `app/src/main/java/com/example/grocerywatch` – application code (DI modules, repository, view model, Compose screens)
- `data` – Room entities and DAOs
- `ui` – Compose screens, navigation, and theming
- `util` – trend calculator and sample data

## Sample data
The Room database pre-populates with three grocery lists, several items, and recent price history entries to demonstrate trends and comparisons on first launch.
