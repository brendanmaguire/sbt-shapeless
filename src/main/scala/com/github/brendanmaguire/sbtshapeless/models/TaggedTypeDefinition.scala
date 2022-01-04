package com.github.brendanmaguire.sbtshapeless.models

import com.github.brendanmaguire.sbtshapeless.models.errors.InvalidTaggedTypeDefinition
import TaggedTypeDefinition.decapitalise

case class TaggedTypeDefinition(tagName: String, tagType: String) {
  val tag = s"${tagName}Tag"
  val lowercaseTagName = decapitalise(tagName)
}

object TaggedTypeDefinition {
  private[models] val tagNameRegex = "(^[A-Z][A-Za-z0-9]*$)".r
  private[models] val tagTypeRegex = "(^[a-zA-Z][a-zA-Z0-9.]*[a-zA-Z0-9]$)".r

  def apply(
      definitionString: String,
      fileLocation: FileLocation,
  ): Either[InvalidTaggedTypeDefinition, TaggedTypeDefinition] =
    definitionString.split(":").map(_.strip).filter(_.nonEmpty).toList match {
      case tagNameRegex(tagName) :: tagTypeRegex(tagType) :: Nil => Right(TaggedTypeDefinition(tagName, tagType))
      case _                                                     => Left(InvalidTaggedTypeDefinition(definitionString, fileLocation))
    }

  private def decapitalise(str: String) = if (str.isEmpty) str else str(0).toLower + str.substring(1)
}

object TaggedTypeDefinitionExpanded {
  def unapply(taggedTypeDefinition: TaggedTypeDefinition): Option[(String, String, String, String)] =
    Some(
      (
        taggedTypeDefinition.tagName,
        taggedTypeDefinition.tagType,
        taggedTypeDefinition.tag,
        taggedTypeDefinition.lowercaseTagName,
      )
    )
}

case class FileLocation(path: String, lineNumber: Int) {
  override def toString: String = s"$path:$lineNumber"
}
