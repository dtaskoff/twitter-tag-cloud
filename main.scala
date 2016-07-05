package ttc

import ttc.TStreamer._
import ttc.TagCloud._

import scala.collection.JavaConverters._
import scala.swing._
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class DrawPanel(image: BufferedImage) extends Panel {
  override def paintComponent(g: Graphics2D) =
    if (image != null) {
      val (w, h) = (image.getWidth(), image.getHeight())
      val ratio = 640.0 / w.toDouble min 480.0 / h.toDouble
      g.drawImage(image, 0, 0, null)
      g.scale(ratio, ratio)
    }
}

class TwitterTagCloudUI(onClose: () => Unit) extends MainFrame {
  title = "Twitter tag cloud"
  preferredSize = new Dimension(640, 480)
  contents = new FlowPanel {
    contents += new DrawPanel(null)
  }
}

object TwitterTagCloud {

  def main(args: Array[String]): Unit = {
    val tstreamer = new TStreamer(1)
    val ui = new TwitterTagCloudUI(tstreamer.close)
    ui.visible = true
    tstreamer.stream()

    Thread sleep 9000
    var wc = tstreamer.getWordCount().asScala.toMap.mapValues(_.toInt)
    println("the image is ready")
    ui.contents = new FlowPanel {
      contents += new DrawPanel(
        tagCloud(wordsWithSizes(mostCommonWords(3)(wc))))
    }
    ui.repaint()
  }

  def test(n: Int) = {
    import ttc.StreamDummy._
    ImageIO.write(
      tagCloud(wordsWithSizes(mostCommonWords(n)(getWordCount))),
      "png", new File("test.png"))
  }

}
