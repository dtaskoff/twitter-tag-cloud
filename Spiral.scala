package ttc


object Spiral {
  val up    = (0, -1)
  val right = (1, 0)
  val down  = (0, 1)
  val left  = (-1, 0)

  def nTimes(n: Int)(d: Direction): Direction = (d._1 * n, d._2 * n)

  type Direction = (Int, Int)

  val clockwise: Stream[Direction] =
    up #:: right #:: down #:: left #:: clockwise

  val counterclockwise: Stream[Direction] =
    up #:: left #:: down #:: right #:: counterclockwise

  val spiralClockwiseFromZero = spiralClockwiseFrom((0, 0))
  val spiralCounterClockwiseFromZero = spiralCounterClockwiseFrom((0, 0))

  def spiralClockwiseFrom(center: (Int, Int)): Stream[(Int, Int)] =
    go(center, clockwise, 1, 1, false)

  def spiralCounterClockwiseFrom(center: (Int, Int)): Stream[(Int, Int)] =
    go(center, counterclockwise, 1, 1, false)

  def spiralClockwiseWithStep(step: Int): Stream[(Int, Int)] =
    spiralClockwiseFromZero.map(nTimes(step) _)

  def spiralCounterClockwiseWithStep(step: Int): Stream[(Int, Int)] =
    spiralCounterClockwiseFromZero.map(nTimes(step) _)

  def go(curr: (Int, Int),
        directions: Stream[Direction],
        n: Int, i: Int, b: Boolean): Stream[(Int, Int)] =

    if (i == 0 && b) go(curr, directions.tail, n + 1, n + 1, false)
    else if (i == 0) go(curr, directions.tail, n, n, true)
    else curr #:: go(directions.head, directions, n, i - 1, b)

}
