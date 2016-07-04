package ttc

import java.awt.{ Color, Font, FontMetrics, Graphics2D }
import java.awt.geom.{ AffineTransform, Area }
import java.awt.image.BufferedImage


object TagCloud {

  val font = new Font("Helvetica", Font.BOLD, 72)
  val color = Color.BLUE

  def mostCommonWords(wordCount: Map[String, Int])(n: Int): List[String] =
    wordCount.toList.sortBy(_._2).map(_._1).reverse.take(n)

  def range(wordCount: Map[String, Int]): (Int, Int) =
    (wordCount.minBy(_._2)._2, wordCount.maxBy(_._2)._2)

  def tagCloud(words: List[String]): BufferedImage = {
    val cloud = tagCloudArea(words)
    val (w, h) = (
      cloud.getBounds2D().getWidth().toInt,
      cloud.getBounds2D().getHeight().toInt)

    withBufferedImage(w, h)(image => {
      withGraphics(image)(g2d => {
        g2d.setColor(color)
        g2d.draw(cloud)
        g2d.fill(cloud)
      })
      image
    })
  }

  def tagCloudArea(words: List[String]): Area =
    words.map(wordToArea).reduceLeft(addWord)

  def wordToArea(word: String): Area =
    withBufferedImage()(image =>
      withGraphics(image)(g2d => {
        val glyph = font.createGlyphVector(g2d.getFontRenderContext(), word)
        val lbounds = glyph.getLogicalBounds()
        val vbounds = glyph.getVisualBounds()

        new Area(glyph.getOutline(
          (lbounds.getX() - vbounds.getX()).toFloat,
          -vbounds.getY().toFloat))
      })
    )

  def withBufferedImage[A](width: Int = 1, height: Int = 1)(
      f: BufferedImage => A): A =
    f(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB))

  def withGraphics[A](image: BufferedImage)(f: Graphics2D => A): A = {
    val g2d = image.createGraphics()
    val ret = f(g2d)
    g2d.dispose()
    ret
  }

  def addWord(cloud: Area, word: Area): Area = {
    val wordw  = word.getBounds2D().getWidth().ceil.toInt
    val wordh  = word.getBounds2D().getHeight().ceil.toInt
    val cloudw = cloud.getBounds2D().getWidth().ceil.toInt
    val cloudh = cloud.getBounds2D().getHeight().ceil.toInt
    val center = ((cloudw - wordw) / 2, (cloudh - wordh) / 2)

    import Spiral._
    var points = spiralFrom(center)
    var placed = false

    while (!placed) {
      val point = points.head
      def translate: (Int, Int) => AffineTransform =
        new AffineTransform(1, 0, 0, 1, _, _)
      val word2 = word.createTransformedArea(translate(point._1, point._2))
      val cloud2 = cloud.createTransformedArea(new AffineTransform())
      cloud2.intersect(word2)

      if (cloud2.isEmpty) {
        cloud.add(word2)
        if (point._1 < 0 && point._2 < 0)
          cloud.transform(translate(-point._1, -point._2))
        else if (point._1 < 0)
          cloud.transform(translate(-point._1, 0))
        else if (point._2 < 0)
          cloud.transform(translate(0, -point._2))
        placed = true
      } else points = points.tail
    }
    cloud
  }

}
