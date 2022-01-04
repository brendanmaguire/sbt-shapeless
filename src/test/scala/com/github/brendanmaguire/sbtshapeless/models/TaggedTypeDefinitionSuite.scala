package com.github.brendanmaguire.sbtshapeless.models

import com.github.brendanmaguire.sbtshapeless.models.TaggedTypeDefinition.{tagNameRegex, tagTypeRegex}
import com.github.brendanmaguire.sbtshapeless.models.errors.InvalidTaggedTypeDefinition
import munit.FunSuite

class TaggedTypeDefinitionSuite extends FunSuite {
  test("tagNameRegex") {
    "UserId" match {
      case tagNameRegex(_*) =>
    }
  }

  test("tagTypeRegex") {
    "Long" match {
      case tagTypeRegex(_*) =>
    }
  }

  private val fileLocation = FileLocation("/path/to/file", 11)

  test("TaggedTypeDefinition parsing success") {
    assertEquals(TaggedTypeDefinition("UserId:Long", fileLocation), Right(TaggedTypeDefinition("UserId", "Long")))
  }

  test("TaggedTypeDefinition parsing failure") {
    val invalidTaggedTypeDefinition = "invalid:invalid"
    assertEquals(
      TaggedTypeDefinition(invalidTaggedTypeDefinition, fileLocation),
      Left(InvalidTaggedTypeDefinition(invalidTaggedTypeDefinition, fileLocation)),
    )
  }
}
