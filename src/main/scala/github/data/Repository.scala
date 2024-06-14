package github
package data

import io.circe.generic.semiauto.deriveCodec
import io.circe.Codec

/**
  * Model of a repository on GitHub
  *
  * @param name
  *   Name of the repository
  */
case class Repository(name: String)

object Repository {
  implicit lazy val contributorCodec: Codec[Repository] = deriveCodec
}
