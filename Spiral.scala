package ttc


object Spiral {
  val up    = (0, -1)
  val right = (1, 0)
  val down  = (0, 1)
  val left  = (-1, 0)

  type Direction = (Int, Int)

  val directions: Stream[Direction] =
    up #:: right #:: down #:: left #:: directions

  def add(l: (Int, Int), r: (Int, Int)): (Int, Int) = (l._1 + r._1, l._2 + r._2)

  def spiralFrom(center: (Int, Int)): Stream[(Int, Int)] = {
    def go(curr: (Int, Int),
          directions: Stream[Direction],
          n: Int, i: Int, b: Boolean): Stream[(Int, Int)] = {
      if (i == 0 && b) go(curr, directions.tail, n + 1, n + 1, false)
      else if (i == 0) go(curr, directions.tail, n, n, true)
      else curr #:: go(add(curr, directions.head), directions, n, i - 1, b)
    }

    go(center, directions, 1, 1, false)
  }
}
