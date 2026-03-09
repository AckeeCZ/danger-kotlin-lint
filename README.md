[![Maven Central](https://img.shields.io/maven-central/v/io.github.ackeecz/danger-kotlin-lint)](https://central.sonatype.com/artifact/io.github.ackeecz/danger-kotlin-lint)

# danger-kotlin lint plugin

Plugin for [danger-kotlin](https://github.com/danger/kotlin) processing outputs
of lint tools ([detekt](https://github.com/detekt/detekt) and [Android Lint](https://developer.android.com/studio/write/lint))

## Installation

Put

```kotlin
@file:DependsOn("io.github.ackeecz:danger-kotlin-lint:x.y.z")
```

to the top of your Dangerfile

## Usage

First you need to register the plugin via

```kotlin
register plugin LintPlugin
```

and then you can use it through its public methods

```kotlin
LintPlugin.findAndProcessDetektReports(DetektConfig)
```

`findAndProcessDetektReports` method accepts an optional config to modify some functionality if needed. See documentation for more details.

### Android Lint

```kotlin
LintPlugin.findAndProcessAndroidLintReports(AndroidLintConfig)
```

`findAndProcessAndroidLintReports` parses Android Lint XML reports and posts inline warnings for issues matching the configured severity levels (Fatal, Error, Warning by default). It also posts Markdown links to HTML lint reports for modules with issues via GitLab CI artifacts.

The method accepts an optional `AndroidLintConfig` to customize discovery (build folders, report folder path, file prefix), report linking (GitLab host, project details, lint job ID), and severity filtering.

Required environment variables for report linking:
- `CI_PROJECT_NAMESPACE` — GitLab project group name
- `CI_PROJECT_NAME` — GitLab project name
- `LINT_JOB_ID_FILE` — path to a file containing the lint CI job ID

Example Dangerfile

```kotlin
@file:DependsOn("io.github.ackeecz:danger-kotlin-lint:x.y.z")

import io.github.ackeecz.danger.lint.LintPlugin
import io.github.ackeecz.danger.lint.detekt.DetektConfig
import io.github.ackeecz.danger.lint.android.AndroidLintConfig

import systems.danger.kotlin.danger
import systems.danger.kotlin.register

import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.stream.Collectors

register plugin LintPlugin

danger(args) {
    LintPlugin.findAndProcessDetektReports(
        // Optional config
        DetektConfig()
    )
    LintPlugin.findAndProcessAndroidLintReports(
        // Optional config
        AndroidLintConfig()
    )
}
```
