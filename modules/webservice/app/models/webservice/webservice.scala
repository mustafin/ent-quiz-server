package models
import slick.lifted.TableQuery
/**
 * Created by Murat.
 */
package object webservice{

  lazy val GameUsers = TableQuery[GameUserTable]
  lazy val Devices = TableQuery[GameUserDevicesTable]
  lazy val Games = TableQuery[GameTable]
  lazy val Rounds = TableQuery[RoundTable]

}
