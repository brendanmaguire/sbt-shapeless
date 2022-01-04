package com.github.brendanmaguire.sbtshapeless.generators

import cats.data.NonEmptyList
import com.github.brendanmaguire.sbtshapeless.models.TaggedTypeDefinition

class CirceCodecsGeneratorSuite extends munit.FunSuite {
  private val packageName = "com.example.application.models"

  test("CirceCodecsGenerator.generate generates the expected code") {
    assertEquals(
      CirceCodecsGenerator.generate(
        packageName = packageName,
        taggedTypeDefinitions = NonEmptyList.of(
          TaggedTypeDefinition("UserId", "Long"),
          TaggedTypeDefinition("UserName", "String"),
        ),
      ),
      s"""package $packageName
         |
         |import io.circe.{Decoder, Encoder}
         |import shapeless.tag
         |import shapeless.tag.@@
         |import com.example.application.models.types._
         |
         |object codecs {
         |  private def taggedTypeDecoder[U, T](implicit decoder: Decoder[U]): Decoder[U @@ T] = decoder.map(tag[T][U](_))
         |  private def taggedTypeEncoder[U, T](implicit encoder: Encoder[U]): Encoder[U @@ T] = encoder.contramap(identity)
         |
         |  implicit def userIdDecoder(implicit decoder: Decoder[Long]): Decoder[UserId] = taggedTypeDecoder[Long, UserIdTag]
         |  implicit def userIdEncoder(implicit encoder: Encoder[Long]): Encoder[UserId] = taggedTypeEncoder[Long, UserIdTag]
         |
         |  implicit def userNameDecoder(implicit decoder: Decoder[String]): Decoder[UserName] = taggedTypeDecoder[String, UserNameTag]
         |  implicit def userNameEncoder(implicit encoder: Encoder[String]): Encoder[UserName] = taggedTypeEncoder[String, UserNameTag]
         |
         |}
         |""".stripMargin,
    )
  }
}
