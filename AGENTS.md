# AGENTS.md

## Build and Test Commands
- Run app: `./gradlew assembleDebug`
- Run single test: `./gradlew test --tests "dev.ch8n.noteflow.ui.MainActivityTest"`
- Lint code: `./gradlew lint`
- Format code: `./gradlew format` 

## Code Style Guidelines
- Kotlin 1.9+ with modern syntax and AndroidX best practices
- Import order: Android first, then third-party libraries
- Use camelCase for variables/functions, PascalCase for classes
- Prefer `val` over `var`; avoid single-letter variables
- Avoid nullable types (`?`) unless necessary
- Error handling: try-catch for I/O, network, parsing
- Functions ≤20 lines; prefer composition over inheritance
- Add Javadoc to public APIs
- Use extension functions for utilities
- Avoid deep nesting (early returns/guard clauses)