package com.github.brendanmaguire.sbtshapeless.generators

import cats.data.NonEmptyList
import com.github.brendanmaguire.sbtshapeless.models.{TaggedTypeDefinition, TaggedTypeDefinitionExpanded}

object DoobieMetasGenerator {
  def generate(packageName: String, taggedTypeDefinitions: NonEmptyList[TaggedTypeDefinition]): String = {
    val metasCode = taggedTypeDefinitions
      .map { case TaggedTypeDefinitionExpanded(tagName, tagType, tag, lowercaseTagName) =>
        s"  implicit def ${lowercaseTagName}Meta(implicit meta: Meta[$tagType]): Meta[$tagName] = taggedTypeMeta[$tagType, $tag]"
      }
      .toList
      .mkString("\n")

    s"""package $packageName
       |
       |import doobie.Meta
       |import shapeless.tag
       |import shapeless.tag.@@
       |import $packageName.types._
       |
       |object metas {
       |  private def taggedTypeMeta[U, T](implicit meta: Meta[U]): Meta[U @@ T] = meta.imap(tag[T][U](_))(identity)
       |
       |$metasCode
       |}
       |""".stripMargin
  }
}
