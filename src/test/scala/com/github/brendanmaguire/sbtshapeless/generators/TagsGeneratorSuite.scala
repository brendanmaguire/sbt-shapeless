package com.github.brendanmaguire.sbtshapeless.generators

import cats.data.NonEmptyList
import com.github.brendanmaguire.sbtshapeless.models.TaggedTypeDefinition

class TagsGeneratorSuite extends munit.FunSuite {
  private val packageName = "com.example.application.models"

  test("TagsGenerator.generate generates the expected code") {
    assertEquals(
      TagsGenerator.generate(
        packageName = packageName,
        taggedTypeDefinitions = NonEmptyList.of(
          TaggedTypeDefinition("UserId", "Long"),
          TaggedTypeDefinition("UserName", "String"),
        ),
      ),
      s"""package $packageName
        |
        |import shapeless.tag
        |import shapeless.tag.@@
        |
        |object types {
        |
        |  sealed trait UserIdTag
        |  type UserId = Long @@ UserIdTag
        |
        |  object UserId {
        |    def apply(userId: Long): UserId =
        |      tag[UserIdTag][Long](userId)
        |
        |    def unapply(userId: Long): Option[UserId] =
        |      Some(apply(userId))
        |  }
        |
        |  sealed trait UserNameTag
        |  type UserName = String @@ UserNameTag
        |
        |  object UserName {
        |    def apply(userName: String): UserName =
        |      tag[UserNameTag][String](userName)
        |
        |    def unapply(userName: String): Option[UserName] =
        |      Some(apply(userName))
        |  }
        |
        |}
        |""".stripMargin,
    )
  }
}
