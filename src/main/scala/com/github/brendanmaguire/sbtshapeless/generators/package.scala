package com.github.brendanmaguire.sbtshapeless

import cats.syntax.either.catsSyntaxEither
import com.github.brendanmaguire.sbtshapeless.models.{FileLocation, GeneratedFileType, TaggedTypeDefinition}
import utils.{NonEmptyListUnapply, when}

import scala.compat.Platform.EOL

package object generators {
  def generateContents(
      sourceFileContents: List[String],
      sourceFilePath: String,
      generateCirceCodecsEnabled: Boolean,
      generateDoobieMetasEnabled: Boolean,
  ): List[(GeneratedFileType, String)] = {
    val fileWithoutBlankLines =
      sourceFileContents
        .map(_.strip)
        .zipWithIndex
        .map { case (taggedTypeDefinitionStr, idx) =>
          (taggedTypeDefinitionStr, FileLocation(sourceFilePath, idx + 1))
        }
        .filter { case (taggedTypeDefinitionStr, _) => taggedTypeDefinitionStr.nonEmpty }

    val packageRegex = "^package ([a-z][a-z.]+[a-z]$)".r

    fileWithoutBlankLines match {
      case packageLine :: NonEmptyListUnapply(taggedTypes) =>
        packageLine match {
          case (packageRegex(packageName), _) =>
            taggedTypes
              .map { case (taggedTypeDefinitionStr, fileLocation) =>
                TaggedTypeDefinition(taggedTypeDefinitionStr, fileLocation)
              }
              .traverse(_.toValidatedNel)
              .fold(
                errors => sys.error(s"${errors.toList.mkString(EOL)}"),
                taggedTypeDefinitions => {
                  List(
                    (GeneratedFileType.Tags, TagsGenerator.generate(packageName, taggedTypeDefinitions))
                  ) ++
                    when(generateCirceCodecsEnabled)(
                      (GeneratedFileType.CirceCodecs, CirceCodecsGenerator.generate(packageName, taggedTypeDefinitions))
                    ) ++
                    when(generateDoobieMetasEnabled)(
                      (GeneratedFileType.DoobieMetas, DoobieMetasGenerator.generate(packageName, taggedTypeDefinitions))
                    )
                },
              )

          case (invalidPackageValue, fileLocation) =>
            sys.error(s"Invalid package value found at $fileLocation: '$invalidPackageValue'")
        }

      case _ => sys.error(s"Tags file must contain a package and tagged types: $sourceFilePath")
    }
  }
}
