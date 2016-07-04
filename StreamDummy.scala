package ttc

import scala.io.Source


object StreamDummy {

  def words: List[String] = {
    val src = Source.fromFile("alice.txt")
    src.getLines.flatMap(_.split("[ .,-?!()'`]+")).
      filter(_.length > 4).map(_.toLowerCase).toList
  }

  def getWordCount: Map[String, Int] =
    words.groupBy(identity).map {
      case (k, v) => (k, v.length)
    }.toMap

}
