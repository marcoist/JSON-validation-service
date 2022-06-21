package controllers

import model.Response.SchemaResult

import javax.inject._
import play.api._
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.mvc._
import service.SchemaService

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class SchemaController @Inject()(
  cc: ControllerComponents,
  service: SchemaService,
)(implicit val ec: ExecutionContext) extends AbstractController(cc) {

  def uploadSchema(schemaId: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val maybeJson = request.body.asJson.map(_.toString())
    maybeJson.map{jsonSchema =>
      service.uploadSchema(schemaId, jsonSchema).map {
        case Some(_) => toSuccessResult(schemaId, "uploadSchema")
        case None => ???
      }
    }.getOrElse(BadRequest(Json
      .toJson(
        SchemaResult(action = "uploadSchema",id = schemaId, status = "error", message = Some("Invalid JSON"))
      )
      .toString()))
  }

  def downloadSchema(schemaId: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    service.downloadSchema(schemaId).map {
      case Some(jsValue) => Ok(jsValue.toString())
      case None => toBadResult("downloadSchema", schemaId, "Schema not found")
    }
  }

  def validateSchema(schemaId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok
  }

  private def toSuccessResult(schemaId: String, action: String) = {
    Ok(
      Json
        .toJson(
          SchemaResult(action = action,id = schemaId, status = "success")
        )
        .toString()
    )
  }

  private def toBadResult(schemaId: String, action: String, error: String) = {
    BadRequest(
      Json
        .toJson(
          SchemaResult(action = action,id = schemaId, status = "error", message = Some(error))
        )
        .toString()
    )
  }
}
