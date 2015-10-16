package controllers

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.language.higherKinds

/**
 * Created by Murat.
 */
object MainTest extends App{


  implicit val expressionConvertable = new JsonConverter[Expression]{
    override def convert(el: Expression): JsonValue = {
      el match {
        case Number(value) => JsonNumber(value)
        case Plus(lhs, rhs) => JsonObject(
          Map("op" -> JsonString("+"),
              "lhs" -> convert(lhs),
              "rhs" -> convert(rhs)
          )
        )
        case Minus(lhs, rhs) => JsonObject(
          Map("op" -> JsonString("-"),
            "lhs" -> convert(lhs),
            "rhs" -> convert(rhs)
          )
        )
      }
    }
  }

  val e = Plus(Number(2), Minus(Number(3), Number(1)))

  print(ExpressionEvaluator.value(e))

  print(JsonWriter.write(e))

}


sealed trait Expression
case class Number(value: Int) extends Expression
case class Plus(lhs: Expression, rhs: Expression) extends Expression
case class Minus(lhs: Expression, rhs: Expression) extends Expression


object ExpressionEvaluator {
  def value(expression: Expression): Int = expression match {
    case Number(value) => value
    case Plus(lhs, rhs) => value(lhs) + value(rhs)
    case Minus(lhs, rhs) => value(lhs) - value(rhs)
  }
}


sealed trait JsonValue
case class JsonObject(entries: Map[String, JsonValue]) extends JsonValue
case class JsonArray(entries: Seq[JsonValue]) extends JsonValue
case class JsonString(value: String) extends JsonValue
case class JsonBoolean(value: Boolean) extends JsonValue
case class JsonNumber(value: BigDecimal) extends JsonValue
case object JsonNull extends JsonValue

object JsonWriter{
  def write(jsVal: JsonValue): String ={

    jsVal match {
      case JsonObject(entries) =>
        val serialized =
          for((key, value) <- entries) yield key + ": " + write(value)
        s"{${serialized.mkString(",")}}"
      case JsonArray(entries) =>
        val serialized = entries map write
        s"[${serialized.mkString(",")}]"
      case JsonString(value) => s""""$value""""
      case JsonBoolean(value) => s""""${value.toString}""""
      case JsonNumber(value) => s""""${value.toString()}""""
      case JsonNull => "null"
    }

  }

  def write[A](value: A)(implicit conv: JsonConverter[A]): String ={
    write(conv.convert(value))
  }
}

trait JsonConverter[-A]{

  def convert(el: A): JsonValue
}



