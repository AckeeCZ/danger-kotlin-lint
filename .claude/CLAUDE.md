# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`danger-kotlin-detekt` is a [danger-kotlin](https://github.com/danger/kotlin) plugin that parses Detekt XML reports and posts inline warnings on pull requests. It is published to Maven Central as `io.github.ackeecz:danger-kotlin-detekt`.

## Commands

```bash
# Build
./gradlew build -q --console=plain

# Run tests
./gradlew test -q --console=plain

# Run a single test class
./gradlew test --tests "io.github.ackeecz.danger.detekt.DetektPluginTest" -q --console=plain

# Run a single test by name
./gradlew test --tests "io.github.ackeecz.danger.detekt.DetektPluginTest.should process two files in report" -q --console=plain

# Run detekt static analysis
./gradlew detektMain -q --console=plain

# Publish to local Maven repo
./gradlew publishToMavenLocal -q --console=plain
```

## Architecture

The library has two layers:

**`src/` — Library source (the published artifact)**
- `DetektPlugin` — The public `DangerPlugin` singleton. Entry point is `findAndProcessReports(Config)`. It delegates file discovery to `FileFinder`, parses each found XML with Jackson XML, and calls `context.warn(message, file, line)` for each Detekt error.
- `FileFinder` — Searches the filesystem for `build/reports/detekt/*.xml` files. Supports two strategies via `BuildFoldersMatcher`: `All` (walks up to depth 6 searching for any `build/` directory) or `Specific` (explicit paths relative to the project root).
- `BuildFoldersMatcher` — Sealed interface with `All` and `Specific` implementations; controls which `build/` directories are scanned.
- Detekt XML reports follow the Checkstyle XML format (`<checkstyle><file name="..."><error .../>`)

**`build-logic/` — Convention Gradle plugins (not published)**
- `DetektPlugin` — Configures detekt with the Ackee detekt config.
- `PublishingPlugin` — Configures Maven Central publishing via `com.vanniktech.maven.publish`.
- Library coordinates and POM metadata come from `LibraryProperties`.

## Testing

Tests use [Kotest](https://kotest.io/) `FunSpec` style with JUnit 5 platform. Test resources (`detekt_result_*.xml`) in `src/test/resources/` are real Detekt XML report fixtures used directly by the tests. The `FakeDangerContext` captures warnings emitted by the plugin.

## Key Conventions

- Kotlin explicit API mode is enforced (`-Xexplicit-api=strict`) — all public declarations must have explicit visibility and return types.
- JVM toolchain target is Java 17.
- All dependency versions are centralized in `gradle/libs.versions.toml`.
