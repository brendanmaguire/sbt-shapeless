package com.github.brendanmaguire.sbtshapeless.generators

import cats.data.NonEmptyList
import com.github.brendanmaguire.sbtshapeless.models.{TaggedTypeDefinition, TaggedTypeDefinitionExpanded}

object CirceCodecsGenerator {

  def generate(packageName: String, taggedTypeDefinitions: NonEmptyList[TaggedTypeDefinition]): String = {
    val codecsCode = taggedTypeDefinitions
      .map { case TaggedTypeDefinitionExpanded(tagName, tagType, tag, lowercaseTagName) =>
        s"""
           |  implicit def ${lowercaseTagName}Decoder(implicit decoder: Decoder[$tagType]): Decoder[$tagName] = taggedTypeDecoder[$tagType, $tag]
           |  implicit def ${lowercaseTagName}Encoder(implicit encoder: Encoder[$tagType]): Encoder[$tagName] = taggedTypeEncoder[$tagType, $tag]
           |""".stripMargin
      }
      .toList
      .mkString("")

    s"""package $packageName
       |
       |import io.circe.{Decoder, Encoder}
       |import shapeless.tag
       |import shapeless.tag.@@
       |import $packageName.types._
       |
       |object codecs {
       |  private def taggedTypeDecoder[U, T](implicit decoder: Decoder[U]): Decoder[U @@ T] = decoder.map(tag[T][U](_))
       |  private def taggedTypeEncoder[U, T](implicit encoder: Encoder[U]): Encoder[U @@ T] = encoder.contramap(identity)
       |$codecsCode
       |}
       |""".stripMargin
  }
}
