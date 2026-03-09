package io.github.ackeecz.danger.lint.detekt

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

internal class DetektReportProcessor(
    private val context: DangerContext,
) {

    private val pathRelativizer = PathRelativizer()

    fun process(reportFiles: List<File>) {
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
        fileInputStream: FileInputStream,
    ): DetektReport {
        return mapper.readValue(
            fileInputStream,
            DetektReport::class.java,
        )
    }

    private fun report(report: DetektReport) {
        report.files.forEach { file ->
            file.errors.forEach { error ->
                val line = error.line.toIntOrNull() ?: 0
                val message = "Detekt: ${error.message}, rule: ${error.source}"
                context.warn(
                    message = message,
                    file = pathRelativizer.relativize(file.name),
                    line = line,
                )
            }
        }
    }
}

@JsonRootName(value = "checkstyle")
@JsonIgnoreProperties(ignoreUnknown = true)
private data class DetektReport(
    @field:JsonProperty("file")
    @field:JacksonXmlElementWrapper(useWrapping = false)
    val files: List<ReportFile> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class ReportFile(
    @field:JacksonXmlProperty val name: String = "",
    @field:JsonProperty("error")
    @field:JacksonXmlElementWrapper(useWrapping = false) val errors: List<ReportError> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class ReportError(
    @field:JacksonXmlProperty val line: String = "",
    @field:JacksonXmlProperty val column: String = "",
    @field:JacksonXmlProperty val severity: String = "",
    @field:JacksonXmlProperty val message: String = "",
    @field:JacksonXmlProperty val source: String = "",
)
