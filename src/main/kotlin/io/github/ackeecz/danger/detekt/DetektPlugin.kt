package io.github.ackeecz.danger.detekt

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import systems.danger.kotlin.sdk.DangerPlugin
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty
import java.io.File
import java.io.FileInputStream

/**
 * Plugin for a danger-kotlin processing outputs of detekt tool.
 */
public object DetektPlugin : DangerPlugin() {

    public override val id: String = "danger-kotlin-detekt"

    /**
     * Parse XML output of detekt and report inline comment
     * to the pull request.
     *
     * @param reportFiles files representing detekt XML output
     */
    public fun parseAndReport(vararg reportFiles: File) {
        val mapper = XmlMapper()
        reportFiles.forEach { file ->
            FileInputStream(file).use { fileInputStream ->
                val report = parse(mapper, fileInputStream)
                report(report)
            }
        }
    }

    private fun parse(
        mapper: XmlMapper,
        fileInputStream: FileInputStream
    ): DetektReport {
        return mapper.readValue(
            fileInputStream,
            DetektReport::class.java
        )
    }

    private fun report(report: DetektReport) {
        report.files.forEach { file ->
            val realFile = File(file.name)
            file.errors.forEach { error ->
                val line = error.line.toIntOrNull() ?: 0
                val message = "Detekt: ${error.message}, rule: ${error.source}"
                val filePath = realFile.absolutePath.removePrefix(
                    "${File("").absolutePath}/"
                )
                context.warn(
                    message = message,
                    file = filePath,
                    line = line
                )
            }
        }
    }
}

@JsonRootName(value = "checkstyle")
@JsonIgnoreProperties(ignoreUnknown = true)
internal data class DetektReport(
    @field:JsonProperty("file")
    @field:JacksonXmlElementWrapper(useWrapping = false)
    val files: List<ReportFile> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class ReportFile(
    @field:JacksonXmlProperty val name: String = "",
    @field:JsonProperty("error")
    @field:JacksonXmlElementWrapper(useWrapping = false) val errors: List<ReportError> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class ReportError(
    @field:JacksonXmlProperty val line: String = "",
    @field:JacksonXmlProperty val column: String = "",
    @field:JacksonXmlProperty val severity: String = "",
    @field:JacksonXmlProperty val message: String = "",
    @field:JacksonXmlProperty val source: String = "",
)
