package model

import play.api.libs.json.{Format, Json}

case class Schema(
  schemaId: String,
  jsonSchema: String,
)

object Schema {
  implicit val schemaFormat: Format[Schema] = Json.format[Schema]
}
