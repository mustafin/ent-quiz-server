package controllers

import scala.concurrent.{Future, Await}
import scala.util.{Failure, Success}


/**
 * Created by Murat.
 */
object MainTest extends App{
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  val d = Future{
    Option("Mrat")
  }
  val t = Future{
    None
  }


  val f = for{
    Some(data) <- d
    tata <- t
    if tata.isDefined
  } yield data + tata.get


  f.recover{case cause => println(cause)}

  f.onComplete{
    case Success(v) => println(v)
    case Failure(exc) => println(exc)
  }

  for(i <- 1 to 1000) i


}
