package io.github.ackeecz.danger.lint.plugin

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue

internal class DetektPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        pluginManager.apply(libs.plugins.detekt)

        val detektConfig: Configuration by configurations.creating {}

        detekt {
            buildUponDefaultConfig = true
            config.setFrom(provider { detektConfig.files })
            ignoreFailures = false
        }

        dependencies {
            detektConfig(libs.ackee.detekt.config.core)
            detektPlugins(libs.detekt.formatting)
        }

    }
}

private fun Project.detekt(action: DetektExtension.() -> Unit) {
    extensions.configure(DetektExtension::class, action)
}

private fun DependencyHandlerScope.detektPlugins(provider: Provider<MinimalExternalModuleDependency>) {
    add("detektPlugins", provider.get())
}
