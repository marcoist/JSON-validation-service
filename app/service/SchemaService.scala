package service

import Commons.Logging
import dao.SchemaDao
import model.Schema
import play.api.libs.json.JsValue

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SchemaService @Inject() (
  val schemaDao: SchemaDao
)(implicit ec: ExecutionContext) extends Logging {

  def uploadSchema(schemaId: String, jsonSchema: String): Future[Option[JsValue]] ={
    val cenas = Schema(schemaId, jsonSchema)
    ???
  }

  def downloadSchema(schemaId: String): Future[Option[JsValue]] = {
    ???
  }

  def validateSchema(schemaId: String, json: JsValue): Future[Option[JsValue]] = {
    ???
  }
}
