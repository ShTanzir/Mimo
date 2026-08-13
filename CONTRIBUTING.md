# Contributing to MIMO

Thanks for your interest in improving MIMO! This is a small, focused project — please read this before opening a PR.

## Before you start

For anything beyond a typo or small bug fix, **open an issue first** and describe what you'd like to change. This avoids wasted work if the change doesn't fit the project's direction (privacy-first, fully offline, no root required).

## Development setup

```bash
git clone https://github.com/ShTanzir/Mimo.git
cd REPO
```

Open in Android Studio (or use `./gradlew assembleDebug` from the CLI). Requirements: JDK 17, Android SDK (compileSdk 34), Kotlin 1.9.24.

## Branch naming

feature/short-description
fix/short-description
docs/short-description


Example: `feature/scheduled-rules`, `fix/snooze-crash-on-api26`

## Commit messages

Keep them short and in the imperative mood:

Add scheduled rules for time-based delays
Fix crash when snooze used after app uninstalled
Update README with v1.2.0 features


## Code style

- Follow standard [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Compose UI: one screen per file/package under `ui/`, ViewModel + StateFlow pattern (see existing screens for reference)
- Prefer immutable data classes for state (`data class XxxUiState`)
- No new third-party analytics, ads, or network dependencies — MIMO stays offline by design
- Add KDoc comments for non-obvious logic (especially in `service/` — the accessibility/foreground service code)

## Pull request process

1. Fork the repo and create your branch from `main`
2. Make your changes, keeping the PR focused on one thing
3. Test on a real device or emulator — the core flow (guard an app → timer → close) can't be fully verified by the CI build alone
4. Update the README/Wiki if your change affects setup, permissions, or user-facing behavior
5. Open the PR against `main` and describe **what** changed and **why**
6. A GitHub Actions build must pass before merge

## What we're looking for

- Bug fixes
- Performance/battery improvements to the accessibility service
- Accessibility (a11y) improvements to the UI itself
- Items from the [Roadmap](../../wiki/Roadmap)

## What we'll likely decline

- Anything that adds network permissions, analytics, or ads
- Anything that reads screen content beyond the foreground package name
- Large refactors without a prior issue discussion

## Questions?

Use [Discussions](../../discussions) for questions or ideas — keep [Issues](../../issues) for actual bugs and concrete feature requests.

Thanks for helping make MIMO better! 🌱
