// Copyright (c) 2013-2017 Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package example

import cats.effect.IO
import fs2.Stream
import doobie._
import doobie.implicits._

// JDBC program using the high-level API
object HiUsage {

  // A very simple data type we will read
  final case class CountryCode(code: Option[String])

  // Program entry point
  def main(args: Array[String]): Unit = {
    val db = Transactor.fromDriverManager[IO]("org.postgresql.Driver", "jdbc:postgresql:world", "postgres", "")
    example.transact(db).unsafeRunSync()
  }

  // An example action. Streams results to stdout
  lazy val example: ConnectionIO[Unit] =
    speakerQuery("English", 10).evalMap(c => FC.delay(println("~> " + s"$c"))).compile.drain

  // Construct an action to find countries where more than `pct` of the population speaks `lang`.
  // The result is a scalaz.stream.Process that can be further manipulated by the caller.
  def speakerQuery(lang: String, pct: Double): Stream[ConnectionIO,CountryCode] =
  sql"SELECT COUNTRYCODE FROM COUNTRYLANGUAGE WHERE LANGUAGE = $lang AND PERCENTAGE > $pct".query[CountryCode].stream

}