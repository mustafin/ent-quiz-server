package models.webservice

import slick.lifted.TableQuery
/**
 * Created by Murat.
 */
object ServiceTables {

  lazy val users = TableQuery[GameUserTable]
  lazy val games = TableQuery[GameTable]
  lazy val rounds = TableQuery[RoundTable]

}
