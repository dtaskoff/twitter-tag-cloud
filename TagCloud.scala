package ttc

import java.awt.{ Color, Font, FontMetrics, Graphics2D }
import java.awt.image.BufferedImage


object TagCloud {

  def mostCommonWords(wordCount: Map[String, Int])(n: Int): List[String] =
    wordCount.toList.sortBy(_._2).map(_._1).reverse.take(n)

  def range(wordCount: Map[String, Int]): (Int, Int) =
    (wordCount.minBy(_._2)._2, wordCount.maxBy(_._2)._2)

  def wordToImage(word: String, font: Font, color: Color): BufferedImage = {
    var (w, h) = getWordDimensions(font, word)
    var img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    drawWord(word, font, color, img)
    img
  }

  def getWordDimensions(font: Font, word: String): (Int, Int) = {
      var img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
      var g2d = img.createGraphics()
      g2d.setFont(font)
      val fm = g2d.getFontMetrics()
      g2d.dispose()
      (fm.stringWidth(word), fm.getHeight())
  }

  def drawWord(word: String, font: Font, color: Color, img: BufferedImage) = {
    var g2d = img.createGraphics()
    g2d.setFont(font)
    g2d.setColor(color)
    g2d.drawString(word, 0, g2d.getFontMetrics().getAscent())
    g2d.dispose()
  }

}
