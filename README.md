[![Maven Central](https://img.shields.io/maven-central/v/io.github.ackeecz/danger-kotlin-lint)](https://central.sonatype.com/artifact/io.github.ackeecz/danger-kotlin-lint)

# danger-kotlin lint plugin

Plugin for [danger-kotlin](https://github.com/danger/kotlin) processing outputs
of lint tools (currently [detekt](https://github.com/detekt/detekt))

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

Example Dangerfile

```kotlin
@file:DependsOn("io.github.ackeecz:danger-kotlin-lint:x.y.z")

import io.github.ackeecz.danger.lint.LintPlugin
import io.github.ackeecz.danger.lint.detekt.DetektConfig

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
}
```
