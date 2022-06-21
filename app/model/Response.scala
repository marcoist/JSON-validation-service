package model

import play.api.libs.json.JsonConfiguration.Aux
import play.api.libs.json.{Format, Json, JsonConfiguration, OptionHandlers}

object Response{
  /**
   * Default Option Handlers
   * Uses readNullable and writesNullable
   */
  implicit val jsonCfg: Aux[Json.MacroOptions] = JsonConfiguration(optionHandlers = OptionHandlers.Default)

  case class SchemaResult(
    action: String,
    id: String,
    status: String,
    message: Option[String] = None
  )

  object SchemaResult{
    implicit val apiConfigFormat: Format[SchemaResult] = Json.format[SchemaResult]
  }
}




