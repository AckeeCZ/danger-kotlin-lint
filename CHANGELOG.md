# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Changed
- Breaking change of `DetektPlugin.parseAndReport` to `DetektPlugin.findAndProcessReports`. On top of previous files parsing
and reporting this method now even finds all Detekt reports. You can now provide an optional config to modify some
functionality if needed. See documentation for more details.
