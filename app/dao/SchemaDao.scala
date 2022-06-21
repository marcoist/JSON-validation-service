package dao

import Commons.Logging
import org.mongodb.scala._
import model.Schema
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.result.InsertOneResult
import play.api.libs.json.JsValue

import scala.concurrent.Future

class SchemaDao extends BaseDao with Logging{


  val collection: MongoCollection[Document] = database.getCollection("jschemas")

  def findSchemaById(schemaId: String): Future[Option[Schema]] ={
    collection.find[Schema](equal("schemaId", schemaId)).first().toFutureOption()
  }

  def insertSchema(schemaId: String, jsonSchema: JsValue): Future[Option[InsertOneResult]] = {
    val dJsonSchema = schemaToDocument(schemaId, jsonSchema)
    collection.insertOne(dJsonSchema).toFutureOption()

  }

  private def schemaToDocument(schemaId: String, jsonSchema: JsValue) = {
    Document(
      "schemaId" -> schemaId,
      "jsonSchema" -> jsonSchema.toString()
    )
  }
}
