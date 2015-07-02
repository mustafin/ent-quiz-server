package models

import sorm._

/**
 * Created by Murat.
 */
object DB extends Instance(

  entities = Seq(Entity[User]()),
  url = "jdbc:myql://localhost:3306/entquiz?useEncoding=true&amp;characterEncoding=UTF-8",
  user = "root",
  password = ""

)
