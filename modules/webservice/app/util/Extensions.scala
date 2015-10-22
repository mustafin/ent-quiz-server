package util

import scala.concurrent.Future
import scala.math.Ordered._
/**
 * Created by Murat.
 */
object Extensions {

  implicit class OptionCompare[T : Ordering](val self: Option[T]) {
    def ===(op: Option[T]) = {
      if(self.isDefined && op.isDefined){
        (self.get compare op.get) == 0
      }else false
    }
  }


}
