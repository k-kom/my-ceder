# Repository Guidelines

## Project Structure & Module Organization

This directory currently contains no application code, tests, assets, or build configuration. Keep the repository root focused on project documentation and configuration. When introducing code, organize related functionality into clearly named modules and document the chosen layout here. Use dedicated directories such as `src/`, `tests/`, and `assets/` only when the selected stack needs them.

## Build, Test, and Development Commands

No build, test, or local development commands are configured yet. When adding the initial toolchain, provide reproducible commands for installing dependencies, running locally, building, and testing. Record the exact commands in this guide and the project README; do not assume commands such as `npm test` or `make build` exist before their configuration is committed.

## Coding Style & Naming Conventions

Follow the conventions of the language and framework introduced by the project. Configure a formatter and linter alongside the first source files, including explicit indentation rules. Use descriptive names, keep modules focused, and avoid unrelated formatting changes. Use lowercase, hyphen-separated names for general documentation files where appropriate; retain standard filenames such as `README.md` and `AGENTS.md`.

## Testing Guidelines

No testing framework or coverage threshold is established. Add appropriate automated tests with new behavior and regression tests with bug fixes. Choose test filenames that match the selected framework, and document how to run individual tests and the full suite. Keep tests deterministic and independent of private credentials.

## Commit & Pull Request Guidelines

There is no Git history available from which to infer commit conventions. Use concise, imperative commit subjects, such as `Add initial project configuration`. Keep each change focused. Pull requests should explain the purpose, summarize changes, list validation performed, and link relevant issues. Include screenshots when changing a user interface.

## Security & Configuration

Never commit secrets or local credentials. When configuration becomes necessary, document required variables and provide example values without sensitive data. Add generated outputs and local configuration to `.gitignore` as tooling is introduced.
