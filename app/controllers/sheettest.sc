val a = getPerson

if(a.get.name == "murat")
  println("asd")
else
  println("F")

class Person(val name:String)

def getPerson: Option[Person] = None

def nu: Person = null