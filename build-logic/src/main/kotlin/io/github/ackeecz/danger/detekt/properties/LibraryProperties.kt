package io.github.ackeecz.danger.detekt.properties

import org.gradle.api.Project
import java.io.File
import java.util.Properties

public class LibraryProperties private constructor(
    private val properties: Properties,
) {

    public val groupId: String = getProperty("GROUP_ID")
    public val pomUrl: String = getProperty("POM_URL")

    public val pomDeveloperId: String = getProperty("POM_DEVELOPER_ID")
    public val pomDeveloperName: String = getProperty("POM_DEVELOPER_NAME")
    public val pomDeveloperEmail: String = getProperty("POM_DEVELOPER_EMAIL")

    public val pomLicenceName: String = getProperty("POM_LICENCE_NAME")
    public val pomLicenceUrl: String = getProperty("POM_LICENCE_URL")

    public val pomScmConnection: String = getProperty("POM_SCM_CONNECTION")
    public val pomScmDeveloperConnection: String = getProperty("POM_SCM_DEVELOPER_CONNECTION")
    public val pomScmUrl: String = getProperty("POM_SCM_URL")

    public val artifactId: String = getProperty("ARTIFACT_ID")
    public val artifactVersion: String = getProperty("VERSION")
    public val artifactPomName: String = getProperty("POM_NAME")
    public val artifactPomYear: String = getProperty("POM_YEAR")
    public val artifactPomDescription: String = getProperty("POM_DESCRIPTION")

    public constructor(project: Project) : this(
        properties = Properties().also {
            it.load(File("${project.rootProject.rootDir}/lib.properties").reader())
        },
    )

    private fun getProperty(name: String) = properties.getNonNull(name)
}

internal fun Properties.getNonNull(name: String) = requireNotNull(getProperty(name))
