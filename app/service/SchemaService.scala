package service

import Commons.Logging
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchemaFactory
import dao.SchemaDao
import play.api.libs.json.{JsNull, JsObject, JsValue, Json}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SchemaService @Inject() (
  val dao: SchemaDao
)(implicit ec: ExecutionContext) extends Logging {

  def uploadSchema(schemaId: String, jsonSchema: JsValue): Future[Option[JsValue]] ={
    dao.insertSchema(schemaId, jsonSchema.toString()).map {
      case Some(result) => if(result.wasAcknowledged()) Some(jsonSchema) else {
        logger.error("Error uploading jsonSchema")
        None
      }
      case None =>
        logger.error("Error uploading jsonSchema")
        None
    }
  }

  def downloadSchema(schemaId: String): Future[Option[JsValue]] = {
    dao.findSchemaById(schemaId).map {
      case Some(schema) =>
        parseJson(schema.jsonSchema)
      case None =>
        logger.error(s"No jsonSchema found for $schemaId")
        None
    }
  }

  def validateSchema(schemaId: String, json: JsValue): Future[Either[String, JsValue]] = {
    dao.findSchemaById(schemaId).map {
      case Some(schema) =>
        validateJson(schema.jsonSchema, json)
      case None =>
        logger.error(s"No jsonSchema found for $schemaId")
        Left(s"No jsonSchema found for $schemaId")
    }
  }

  private def validateJson(jsonSchema: String, json: JsValue): Either[String, JsValue] ={
    val updatedJson = removeNullFromJson(json)

    (for {
      jSchemaNone <- buildJsonNode(jsonSchema)
      jsonNode <- buildJsonNode(updatedJson.toString())
    } yield {
      val result = JsonSchemaFactory.byDefault.getJsonSchema(jSchemaNone).validate(jsonNode)
      if(result.isSuccess) Right(updatedJson) else {
        var message = ""
        val iterator = result.iterator()
        while (iterator.hasNext){
          message = s"$message ${iterator.next().getMessage}"
        }
        Left(message)
      }
    }).getOrElse(Left("Error parsing the Json"))
  }

  private def buildJsonNode(json: String): Option[JsonNode] = {
    try {
      Some(JsonLoader.fromString(json))
    }catch {
      case _: JsonParseException =>
        logger.error("Unable to parse json")
        None
    }
  }

  private def parseJson(json: String): Option[JsValue] = {
    try {
      Some(Json.parse(json))
    }catch {
      case _: JsonParseException =>
        logger.error("Unable to parse json")
        None
    }
  }

  private def removeNullFromJson(json: JsValue): JsValue =
    json match {
      case JsObject(f) =>
        JsObject(f.flatMap {
          case (_, JsNull) => None
          case _ @ (name, nestedValue) =>
            Some(name -> removeNullFromJson(nestedValue))
        })
      case other => other
    }
}
