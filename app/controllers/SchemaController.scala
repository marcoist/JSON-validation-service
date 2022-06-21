package controllers

import model.Response.SchemaResult

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import service.SchemaService

import scala.concurrent.{ExecutionContext, Future}

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
    val maybeJson = request.body.asJson
    maybeJson.map{jsonSchema =>
      service.uploadSchema(schemaId, jsonSchema).map {
        case Some(_) => toCreatedResult(schemaId, "uploadSchema")
        case None => toBadResult("uploadSchema", schemaId, "Error uploading the schema")
      }
    }.getOrElse(Future.successful(toBadResult("uploadSchema", schemaId, "Invalid JSON")))
  }

  def downloadSchema(schemaId: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    service.downloadSchema(schemaId).map {
      case Some(jsValue) => Ok(jsValue.toString())
      case None => toNotFoundResult("downloadSchema", schemaId, "Schema not found")
    }
  }

  def validateSchema(schemaId: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val maybeJson = request.body.asJson
    maybeJson.map{json =>
      service.validateSchema(schemaId, json).map {
        case Left(error) if error.contains(schemaId) => toNotFoundResult("validateSchema", schemaId, error)
        case Left(error) => toBadResult("validateSchema", schemaId, error)
        case Right(_) => toSuccessResult(schemaId, "validateSchema")
      }
    }.getOrElse(Future.successful(toBadResult("validateSchema", schemaId, "Invalid JSON")))
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

  private def toCreatedResult(schemaId: String, action: String) = {
    Created(
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

  private def toNotFoundResult(schemaId: String, action: String, error: String) = {
    NotFound(
      Json
        .toJson(
          SchemaResult(action = action,id = schemaId, status = "error", message = Some(error))
        )
        .toString()
    )
  }
}
