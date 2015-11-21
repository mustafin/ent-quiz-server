trait B{
  def go()
}

trait A extends B{
  def go = "GKO"
}

class Murat{
  self: B =>

  def asdasd = self.go()


}


val t = new Murat with A