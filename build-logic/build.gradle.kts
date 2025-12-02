plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xexplicit-api=strict")
    }
}

dependencies {
    compileOnly(files(libs::class.java.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.mavenPublish.gradlePlugin)
}

gradlePlugin {
    plugins {
        plugin(
            dependency = libs.plugins.ackeecz.danger.detekt.detekt,
            pluginClassSimpleName = "DetektPlugin",
        )
        plugin(
            dependency = libs.plugins.ackeecz.danger.detekt.publishing,
            pluginClassSimpleName = "PublishingPlugin",
        )
    }
}

private fun NamedDomainObjectContainer<PluginDeclaration>.plugin(
    dependency: Provider<out PluginDependency>,
    pluginClassSimpleName: String,
) {
    val pluginId = dependency.get().pluginId
    register(pluginId) {
        id = pluginId
        implementationClass = "io.github.ackeecz.danger.detekt.plugin.$pluginClassSimpleName"
    }
}