package ttc


object TagCloud {

  def mostCommonWords(
      wordCount: Map[String, Int])(n: Int): List[String] =
    wordCount.toList.sortBy(_._2).map(_._1).reverse.take(n)

}
