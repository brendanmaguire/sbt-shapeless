package com.github.brendanmaguire.sbtshapeless.models

sealed trait GeneratedFileType
object GeneratedFileType {
  object Tags extends GeneratedFileType
  object CirceCodecs extends GeneratedFileType
  object DoobieMetas extends GeneratedFileType
}
