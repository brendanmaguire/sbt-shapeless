package com.github.brendanmaguire.sbtshapeless.models

object errors {
  case class InvalidTaggedTypeDefinition(definitionString: String, fileLocation: FileLocation) {
    override def toString: String = s"Invalid tagged type definition at $fileLocation - '$definitionString''"
  }
}
