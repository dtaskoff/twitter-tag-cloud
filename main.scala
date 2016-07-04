package ttc

import ttc.TStreamer._
import ttc.TagCloud._

import scala.collection.JavaConverters._
import java.io.File
import javax.imageio.ImageIO


object TwitterTagCloud {

  def main(args: Array[String]) = {
    val tstreamer = new TStreamer(1)
    tstreamer.stream()
    Thread sleep 60000
    var wc = tstreamer.getWordCount().asScala.toMap.mapValues(_.toInt)
    ImageIO.write(tagCloud(wordsWithSizes(mostCommonWords(3)(wc))),
      "png", new File("01.png"))

    Thread sleep 60000
    wc = tstreamer.getWordCount().asScala.toMap.mapValues(_.toInt)
    ImageIO.write(tagCloud(wordsWithSizes(mostCommonWords(3)(wc))),
      "png", new File("02.png"))

    tstreamer.close()
  }

}
