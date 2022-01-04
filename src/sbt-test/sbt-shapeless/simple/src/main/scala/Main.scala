import com.example.model.types.{UserId, UserName}
import com.example.model.codecs._
import com.example.model.metas._
import com.example.service.types.ServiceUri
import com.example.service.codecs._
import com.example.service.metas._
import doobie.Meta
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.net.URI
import scala.util.Try

object Main extends App {
  val long = 1L
  val string = "Mary"
  val uri = new URI("http://example.com/abc")

  // Test apply
  val userDetails = UserDetails(UserId(long), UserName(string), ServiceUri(uri))

  // Test unapply
  val result = userDetails match {
    case UserDetails(UserId(userIdValue), UserName(userNameValue), ServiceUri(serviceUriValue)) =>
      (userIdValue, userNameValue, serviceUriValue)
    case _ => throw new Exception("Should not get here")
  }

  assert(result == (long, string, uri))

  // Test Circe Codecs
  implicit val uriDecoder: Decoder[URI] = Decoder[String].emapTry(uriStr => Try(URI.create(uriStr)))
  implicit val uriEncoder: Encoder[URI] = Encoder[String].contramap(_.toString)

  val userDetailsEncoder = deriveEncoder[UserDetails]

  val json = userDetailsEncoder(userDetails)
  assert(deriveDecoder[UserDetails].decodeJson(json) == Right(userDetails))

  // Test Doobie Metas
  implicitly[Meta[UserId]]
  implicitly[Meta[UserName]]

  implicit def metaUri: Meta[URI] = Meta[String].imap(URI.create)(_.toString)
  implicitly[Meta[ServiceUri]]
}

case class UserDetails(userId: UserId, userName: UserName, serviceUri: ServiceUri)
