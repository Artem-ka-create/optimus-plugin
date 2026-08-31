<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# optimus-plugin Changelog

## [Unreleased]

## [1.0.0] - 2026-08-31
### Added
- Real-time accessibility inspection (`GlobalAccessibilityInspection`) via a single PSI tree traversal that checks every active rule in one pass, including elements inside injected template strings.
- 27 accessibility rules covering WCAG & ARIA standards, grouped into General, Anchor, ARIA, and Role categories.
- Automated quick fixes to add/remove attributes, remove elements, and rename tags.
- Multi-framework support for Angular, React (JSX/TSX), Vue, and vanilla HTML/JS/TS, distinguishing native HTML elements from framework components.
- Settings page (**Settings > Editor > Optimus Accessibility**) to enable/disable individual rules, grouped by category.
- Project-level JSON configuration file support to control rule state outside the IDE, taking priority over manual settings when enabled.
- Export current rule configuration to a shareable `optimus-accessibility.json` file.
- Multi-module plugin architecture separating the core implementation (`core`) from the plugin assembly module.

Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).
