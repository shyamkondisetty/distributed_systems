package micro.services

import scala.Predef._

/** This object was generated by sbt-buildinfo. */
case object BuildInfo {
  /** The value is "distributedbroker". */
  val name: String = "distributedbroker"
  /** The value is "0.1-SNAPSHOT". */
  val version: String = "0.1-SNAPSHOT"
  override val toString: String = {
    "name: %s, version: %s" format (
      name, version
    )
  }
}