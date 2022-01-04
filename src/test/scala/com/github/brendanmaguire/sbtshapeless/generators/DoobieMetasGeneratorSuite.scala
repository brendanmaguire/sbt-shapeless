package com.github.brendanmaguire.sbtshapeless.generators

import cats.data.NonEmptyList
import com.github.brendanmaguire.sbtshapeless.models.TaggedTypeDefinition

class DoobieMetasGeneratorSuite extends munit.FunSuite {
  private val packageName = "com.example.application.models"

  test("DoobieMetasGenerator.generate generates the expected code") {
    assertEquals(
      DoobieMetasGenerator.generate(
        packageName = packageName,
        taggedTypeDefinitions = NonEmptyList.of(
          TaggedTypeDefinition("UserId", "Long"),
          TaggedTypeDefinition("UserName", "String"),
        ),
      ),
      s"""package $packageName
         |
         |import doobie.Meta
         |import shapeless.tag
         |import shapeless.tag.@@
         |import com.example.application.models.types._
         |
         |object metas {
         |  private def taggedTypeMeta[U, T](implicit meta: Meta[U]): Meta[U @@ T] = meta.imap(tag[T][U](_))(identity)
         |
         |  implicit def userIdMeta(implicit meta: Meta[Long]): Meta[UserId] = taggedTypeMeta[Long, UserIdTag]
         |  implicit def userNameMeta(implicit meta: Meta[String]): Meta[UserName] = taggedTypeMeta[String, UserNameTag]
         |}
         |""".stripMargin,
    )
  }
}
