<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Changelist-router Changelog

## [Unreleased]

## [1.0.3]

### Added

- Existing changes in the default changelist are now re-routed when routing rules are added or modified

### Fixed

- Fixed binary incompatibility with IntelliJ IDEA 2023.2–2024.1 caused by Java 21 `MatchException` class reference
- Glob pattern `**` now correctly matches zero subdirectories (e.g. `test/**/*.ts` matches `test/first.ts`)

## [1.0.2]

### Fixed

- Lowered minimum IDE version from 2025.2 to 2023.2 for wider compatibility
- Removed upper IDE version cap to support IntelliJ 2025.3+ and future versions

## [1.0.1]

### Changed

- Updated README.md

## [1.0.0]

### Added

- Automatic routing of VCS changes to changelists based on file path patterns
- Support for Glob and Regex pattern matching
- Case-sensitivity toggle per route mapping
- Settings UI under VCS > Changelist Router
- Live test path matching in settings panel

[Unreleased]: https://github.com/MreeP/Changelist-router/compare/1.0.3...HEAD
[1.0.3]: https://github.com/MreeP/Changelist-router/compare/1.0.2...1.0.3
[1.0.2]: https://github.com/MreeP/Changelist-router/compare/1.0.1...1.0.2
[1.0.1]: https://github.com/MreeP/Changelist-router/compare/1.0.0...1.0.1
[1.0.0]: https://github.com/MreeP/Changelist-router/commits/1.0.0
