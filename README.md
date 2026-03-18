# Changelist Router

[![Build](https://github.com/MreeP/Changelist-router/workflows/Build/badge.svg)](https://plugins.jetbrains.com/plugin/30779)
[![Version](https://img.shields.io/jetbrains/plugin/v/30779.svg)](https://plugins.jetbrains.com/plugin/30779)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/30779.svg)](https://plugins.jetbrains.com/plugin/30779)

<!-- Plugin description -->
Changelist Router is an IntelliJ Platform plugin that automatically routes VCS changes to the correct changelist based on file path patterns. Define rules using glob or regex patterns and let the plugin sort your changes for you.
<!-- Plugin description end -->

## Features

- Pattern-based routing — define rules that match file paths using glob or regex patterns
- Automatic changelist assignment — newly detected VCS changes are routed to the matching changelist
- Case-sensitivity control — toggle case-sensitive matching per route
- Live test matching — verify your patterns against test paths directly in the settings panel

## Installation

- From the IDE:
  <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > search for "Changelist Router" > <kbd>Install</kbd>

- Manual install:
  Download the [latest release](https://github.com/MreeP/Changelist-router/releases/latest) ZIP and install via
  <kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>

## Usage

1. Open <kbd>Settings</kbd> > <kbd>Version Control</kbd> > <kbd>Changelist Router</kbd>
2. Add route mappings — each mapping consists of:
   - A file path pattern (glob or regex)
   - A target changelist name
   - An optional case-sensitivity toggle
3. When a file change is detected, the plugin matches it against your patterns and moves it to the corresponding changelist.
