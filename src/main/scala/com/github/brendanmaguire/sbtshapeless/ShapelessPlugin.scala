package com.github.brendanmaguire.sbtshapeless

import com.github.brendanmaguire.sbtshapeless.generators.generateContents
import com.github.brendanmaguire.sbtshapeless.models.GeneratedFileType
import org.apache.commons.io.FilenameUtils
import sbt._
import sbt.Keys._
import sbt.nio.Keys.fileInputs
import sbt.plugins.JvmPlugin

import java.nio.file.Path

object ShapelessPlugin extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  object autoImport {
    val shapelessTagsSourceDirectory = settingKey[File]("Default shapeless tags source directory.")
    val shapelessGenerate = taskKey[Seq[File]]("Generate shapeless tags")

    val shapelessCirceCodecsEnabled =
      settingKey[Boolean]("If true then circe encoders and decoders will be generated for the shapeless tags")

    val shapelessDoobieMetasEnabled =
      settingKey[Boolean]("If true then doobie metas will be generated for the shapeless tags")
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    shapelessCirceCodecsEnabled := false,
    shapelessDoobieMetasEnabled := false,
    shapelessTagsSourceDirectory := sourceDirectory.value / "shapeless-tags",
    shapelessGenerate / fileInputs += shapelessTagsSourceDirectory.value.toGlob / RecursiveGlob / "*.tags",
    shapelessGenerate := {
      def getOutputTarget(source: Path): GeneratedFileType => File = {
        val sourceRelativeToSourceDirectory = shapelessTagsSourceDirectory.value.toPath.relativize(source)
        val targetDirectory = FilenameUtils.removeExtension(sourceRelativeToSourceDirectory.toString)
        def file(name: String) = (Compile / sourceManaged).value / s"$targetDirectory/$name.scala"

        {
          case GeneratedFileType.Tags        => file("types")
          case GeneratedFileType.CirceCodecs => file("codecs")
          case GeneratedFileType.DoobieMetas => file("metas")
        }
      }

      shapelessGenerate.inputFiles
        .flatMap { source =>
          val sourceFile = source.toFile
          val sourceFileContents = IO.readLines(sourceFile)

          val target = getOutputTarget(source)

          generateContents(
            sourceFileContents,
            sourceFilePath = sourceFile.getPath,
            generateCirceCodecsEnabled = shapelessCirceCodecsEnabled.value,
            generateDoobieMetasEnabled = shapelessDoobieMetasEnabled.value,
          )
            .map { case (fileType, contents) =>
              val generatedContentFile = target(fileType)
              IO.write(generatedContentFile, contents)
              generatedContentFile
            }
        }
    },
    (Compile / sourceGenerators) += shapelessGenerate,
  )

  override lazy val buildSettings = Seq()

  override lazy val globalSettings = Seq()
}
