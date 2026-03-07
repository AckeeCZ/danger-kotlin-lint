package io.github.ackeecz.danger.lint.android

/**
 * Severity levels of Android Lint issues.
 */
public enum class AndroidLintSeverity(internal val xmlValue: String) {

    Fatal("Fatal"),
    Error("Error"),
    Warning("Warning"),
    Information("Hint"),
    Ignore("Ignore"),
}
