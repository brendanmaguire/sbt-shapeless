package com.github.brendanmaguire.sbtshapeless

import cats.syntax.list.catsSyntaxList

object utils {
  object NonEmptyListUnapply {
    def unapply[A](list: List[A]) = list.toNel
  }

  def when[A](cond: Boolean)(a: => A): List[A] = if (cond) List(a) else List.empty
}
