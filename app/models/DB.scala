package models

import sorm._

/**
 * Created by Murat.
 */
object DB extends Instance(entities = Seq(Entity[Person](),
  url = "jdbc:mysql://localhost:3306/entquiz?useEncoding=true&amp;characterEncoding=UTF-8")){

}
