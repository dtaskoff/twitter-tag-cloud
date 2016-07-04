package ttc

import java.awt.{ Color, Font, FontMetrics, Graphics2D }
import java.awt.geom.{ AffineTransform, Area }
import java.awt.image.BufferedImage


object TagCloud {

  val font: Int => Font = new Font("Helvetica", Font.BOLD, _)
  val FONT_MIN = 14
  val FONT_MAX = 96
  val color = Color.BLUE

  def mostCommonWords(n: Int)(
      wordCount: Map[String, Int]): List[(String, Int)] =
    wordCount.toList.sortBy(_._2).reverse.take(n)

  def wordsWithSizes(wordCount: List[(String, Int)]): List[(String, Int)] = {
    val (x, y) = coeffs(wordCount)
    wordCount map Function.tupled((w, n) => (w, (x * n + y).toInt))
  }

  def coeffs(wordCount: List[(String, Int)]): (Double, Double) = {
    val (rmin, rmax) = range(wordCount)
    val x = (FONT_MAX - FONT_MIN) / (rmax - rmin max 1).toDouble
    val y = FONT_MAX - rmax.toDouble * x
    (x, y)
  }

  def range(wordCount: List[(String, Int)]): (Int, Int) =
    (wordCount.minBy(_._2)._2, wordCount.maxBy(_._2)._2)

  def tagCloud(words: List[(String, Int)]): BufferedImage = {
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

  def tagCloudArea(words: List[(String, Int)]): Area =
    words.map(Function.tupled(wordToArea)).reduceLeft(addWord)

  def wordToArea(word: String, fontSize: Int): Area =
    withBufferedImage()(image =>
      withGraphics(image)(g2d => {
        val glyph =
          font(fontSize).createGlyphVector(g2d.getFontRenderContext(), word)
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
    val (wordw, wordh) = bounds(word)
    val cloudb = bounds(cloud)
    val (cloudw, cloudh) = cloudb
    val center = ((cloudw - wordw) / 2, (cloudh - wordh) / 2)

    var translation = center
    word.transform(translate(center))

    import Spiral._
    var placed = false
    var points = spiralClockwiseFromZero

    while (!placed) {
      val point = points.head
      translation = (translation._1 + point._1, translation._2 + point._2)
      word.transform(translate(point))

      val cloud2 = cloud.createTransformedArea(new AffineTransform())
      cloud2.intersect(word)

      if (cloud2.isEmpty) {
        cloud.add(word)
        cloud.transform(translate(reposition(cloud)(translation)))
        placed = true
      } else points = points.tail
    }
    cloud
  }

  def bounds(area: Area): (Int, Int) = {
    val bs = area.getBounds2D()
    (bs.getWidth().ceil.toInt, bs.getHeight().ceil.toInt)
  }

  def translate: ((Int, Int)) => AffineTransform =
    Function.tupled(new AffineTransform(1, 0, 0, 1, _, _))

  def reposition(area: Area)(t: (Int, Int)): (Int, Int) =
    if (t._1 < 0 && t._2 < 0) (-t._1, -t._2)
    else if (t._1 < 0) (-t._1, 0)
    else if (t._2 < 0) (0, -t._2)
    else (0, 0)

}
