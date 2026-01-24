[ ![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.ackeecz/danger-kotlin-detekt/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.ackeecz/danger-kotlin-detekt)

# danger-kotlin detekt plugin

Plugin for [danger-kotlin](https://github.com/danger/kotlin) processing outputs
of [detekt](https://github.com/detekt/detekt) tool

## Installation

Put

```kotlin
@file:DependsOn("io.github.ackeecz:danger-kotlin-detekt:x.y.z")
```

to the top of your Dangerfile

## Usage

First you need to register the plugin via

```kotlin
register plugin DetektPlugin
```

and then you can use it through it's single public method

```kotlin
DetektPlugin.findAndProcessReports(DetektPlugin.Config)
```

`findAndProcessReports` method accepts an optional config to modify some functionality if needed. See documentation for more details.

Example Dangerfile

```kotlin
@file:DependsOn("io.github.ackeecz:danger-kotlin-detekt:x.y.z")

import io.github.ackeecz.danger.detekt.DetektPlugin

import systems.danger.kotlin.danger
import systems.danger.kotlin.register

import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.stream.Collectors

register plugin DetektPlugin

danger(args) {
    DetektPlugin.findAndProcessReports(
        // Optional config
        DetektPlugin.Config()
    )
}
```
