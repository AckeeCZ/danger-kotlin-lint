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
import java.nio.file.Paths

/**
 * Plugin for a danger-kotlin processing outputs of detekt tool.
 */
public object DetektPlugin : DangerPlugin() {

    public override val id: String = "danger-kotlin-detekt"

    /**
     * Finds and parses XML outputs of Detekt and reports inline comment to the pull request.
     *
     * @param config Config of report processing
     */
    @Suppress("SpreadOperator")
    public fun findAndProcessReports(config: Config = Config()) {
        println("Starting find...")
        val foundFiles = FileFinder.findFiles(
            rootDirectoryPath = Paths.get(""),
            config = config.fileDiscovery,
        )
        println("Find finished")
        parseAndReport(*foundFiles.toTypedArray())
    }

    internal fun parseAndReport(vararg reportFiles: File) {
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

    /**
     * Config of the [DetektPlugin]
     *
     * @param fileDiscovery Config of the file discovery.
     */
    public class Config(
        public val fileDiscovery: FileDiscovery = FileDiscovery(),
    ) {

        /**
         * Configuration of Detekt report files discovery
         *
         * @param buildFoldersMatcher Allows to configure a matcher for build folders. Default is [BuildFoldersMatcher.All].
         * @param detektFolderPath Allows to configure a path to the folder that contains Detekt reports. This path
         * must be relative to the `build` directory.
         */
        public class FileDiscovery(
            public val buildFoldersMatcher: BuildFoldersMatcher = BuildFoldersMatcher.All,
            public val detektFolderPath: String = "reports/detekt",
        )
    }
}

@JsonRootName(value = "checkstyle")
@JsonIgnoreProperties(ignoreUnknown = true)
private data class DetektReport(
    @field:JsonProperty("file")
    @field:JacksonXmlElementWrapper(useWrapping = false)
    val files: List<ReportFile> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class ReportFile(
    @field:JacksonXmlProperty val name: String = "",
    @field:JsonProperty("error")
    @field:JacksonXmlElementWrapper(useWrapping = false) val errors: List<ReportError> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class ReportError(
    @field:JacksonXmlProperty val line: String = "",
    @field:JacksonXmlProperty val column: String = "",
    @field:JacksonXmlProperty val severity: String = "",
    @field:JacksonXmlProperty val message: String = "",
    @field:JacksonXmlProperty val source: String = "",
)
