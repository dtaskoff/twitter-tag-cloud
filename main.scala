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

    sys.ShutdownHookThread {
      tstreamer.close()
    }

    var count = 0
    while (true) {
      try {
        Thread sleep 60000
      } catch {
        case e: InterruptedException => println("gotcha")
      }

      var wc = tstreamer.getWordCount().asScala.toMap.mapValues(_.toInt)
      if (!wc.isEmpty) {
        ImageIO.write(tagCloud(wordsWithSizes(mostCommonWords(10)(wc))),
          "png", new File(s"$count.png"))
        count = count + 1
      }
    }
  }

}
