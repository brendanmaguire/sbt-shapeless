package com.github.brendanmaguire.sbtshapeless.generators

import cats.data.NonEmptyList
import com.github.brendanmaguire.sbtshapeless.models.{TaggedTypeDefinition, TaggedTypeDefinitionExpanded}

object TagsGenerator {
  def generate(packageName: String, taggedTypeDefinitions: NonEmptyList[TaggedTypeDefinition]): String = {
    val tagsCode = taggedTypeDefinitions
      .map { case TaggedTypeDefinitionExpanded(tagName, tagType, tag, lowercaseTagName) =>
        s"""
           |  sealed trait $tag
           |  type $tagName = $tagType @@ $tag
           |
           |  object $tagName {
           |    def apply($lowercaseTagName: $tagType): $tagName =
           |      tag[$tag][$tagType]($lowercaseTagName)
           |
           |    def unapply($lowercaseTagName: $tagType): Option[$tagName] =
           |      Some(apply($lowercaseTagName))
           |  }
           |""".stripMargin
      }
      .toList
      .mkString("")

    s"""package $packageName
       |
       |import shapeless.tag
       |import shapeless.tag.@@
       |
       |object types {
       |$tagsCode
       |}
       |""".stripMargin
  }
}
