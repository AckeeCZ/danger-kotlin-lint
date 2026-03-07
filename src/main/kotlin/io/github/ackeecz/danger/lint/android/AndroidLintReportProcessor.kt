package io.github.ackeecz.danger.lint.android

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import io.github.ackeecz.danger.lint.PathRelativizer
import systems.danger.kotlin.sdk.DangerContext
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty
import java.io.File
import java.io.FileInputStream

internal class AndroidLintReportProcessor(
    private val context: DangerContext,
) {

    private val pathRelativizer = PathRelativizer()

    fun process(reportFiles: List<File>, severities: Set<AndroidLintSeverity>): Set<String> {
        val mapper = XmlMapper()
        val reportPathsWithIssues = mutableSetOf<String>()
        reportFiles.forEach { file ->
            FileInputStream(file).use { fileInputStream ->
                val report = mapper.readValue(fileInputStream, AndroidLintReport::class.java)
                val hasMatchingIssues = report(report, severities)
                if (hasMatchingIssues) {
                    reportPathsWithIssues.add(file.absolutePath.removeSuffix(".xml"))
                }
            }
        }
        return reportPathsWithIssues
    }

    private fun report(report: AndroidLintReport, severities: Set<AndroidLintSeverity>): Boolean {
        var hasMatchingIssues = false
        report.issues.forEach { issue ->
            val location = issue.locations.firstOrNull()
            val severity = AndroidLintSeverity.entries.find { it.xmlValue == issue.severity }

            val message = if (severity == null) {
                "Android Lint [Unknown severity: ${issue.severity}]: ${issue.message} (${issue.id})"
            } else if (severity in severities) {
                "Android Lint [${severity.name}]: ${issue.message} (${issue.id})"
            } else {
                null
            }

            if (message != null) {
                hasMatchingIssues = true
                val line = location?.line?.toIntOrNull()
                if (location != null && line != null) {
                    context.warn(message = message, file = pathRelativizer.relativize(location.file), line = line)
                } else {
                    context.warn(message = message)
                }
            }
        }
        return hasMatchingIssues
    }
}

@JsonRootName(value = "issues")
@JsonIgnoreProperties(ignoreUnknown = true)
private data class AndroidLintReport(
    @field:JsonProperty("issue")
    @field:JacksonXmlElementWrapper(useWrapping = false)
    val issues: List<AndroidLintIssue> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class AndroidLintIssue(
    @field:JacksonXmlProperty val id: String = "",
    @field:JacksonXmlProperty val severity: String = "",
    @field:JacksonXmlProperty val message: String = "",
    @field:JsonProperty("location")
    @field:JacksonXmlElementWrapper(useWrapping = false)
    val locations: List<AndroidLintLocation> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class AndroidLintLocation(
    @field:JacksonXmlProperty val file: String = "",
    @field:JacksonXmlProperty val line: String = "",
    @field:JacksonXmlProperty val column: String = "",
)
