# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Changed
- Renamed artifact from `danger-kotlin-detekt` to `danger-kotlin-lint` (`io.github.ackeecz:danger-kotlin-lint`)
- Renamed `DetektPlugin` to `LintPlugin` as the public entry point (`io.github.ackeecz.danger.lint.LintPlugin`)
- Renamed `DetektPlugin.findAndProcessReports` to `LintPlugin.findAndProcessDetektReports`
- Moved `BuildFoldersMatcher` to `io.github.ackeecz.danger.lint` package
- Extracted `DetektConfig` to `io.github.ackeecz.danger.lint.detekt.DetektConfig` (replaces `DetektPlugin.Config`)

## [1.0.1] - 2026-01-26
### Fixed
- Remove debug prints

## [1.0.0] - 2026-01-24
### Changed
- Breaking change of `DetektPlugin.parseAndReport` to `DetektPlugin.findAndProcessReports`. On top of previous files parsing
  and reporting this method now even finds all Detekt reports. You can now provide an optional config to modify some
  functionality if needed. See documentation for more details.
