import org.scalatest.Matchers._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.Helpers._

import scala.concurrent.duration._
import scala.language.postfixOps

class ApplicationSpec extends PlaySpec with ScalaFutures with GuiceOneServerPerSuite {
  "Application" should {
    val wsClient = app.injector.instanceOf[WSClient]
    val myPublicAddress = s"localhost:$port"

    "send 404 on a bad request" in {
      val testURL = s"http://$myPublicAddress/schema/test"

      whenReady(wsClient.url(testURL).get(), Timeout(1 second)) { response =>
        response.status mustBe NOT_FOUND
      }
    }

    "upload jsonSchema and download" in {
      val testURL = s"http://$myPublicAddress/schema/test1"

      val body = Json.parse("{\n  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n  \"type\": \"object\",\n  \"properties\": {\n    \"source\": {\n      \"type\": \"string\"\n    },\n    \"destination\": {\n      \"type\": \"string\"\n    },\n    \"timeout\": {\n      \"type\": \"integer\",\n      \"minimum\": 0,\n      \"maximum\": 32767\n    },\n    \"chunks\": {\n      \"type\": \"object\",\n      \"properties\": {\n        \"size\": {\n          \"type\": \"integer\"\n        },\n        \"number\": {\n          \"type\": \"integer\"\n        }\n      },\n      \"required\": [\"size\"]\n    }\n  },\n  \"required\": [\"source\", \"destination\"]\n}")

      whenReady(wsClient.url(testURL).post(body), Timeout(1 second)) { response =>
        response.status mustBe CREATED

        whenReady(wsClient.url(testURL).get(), Timeout(1 second)) { response2 =>
          println(response2.body)
          response2.status mustBe OK
        }
      }
    }

    "validate json against jsonSchema" in {
      val testURL = s"http://$myPublicAddress/validate/test1"

      val body = Json.parse("{\n  \"source\": \"/home/alice/image.iso\",\n  \"destination\": \"/mnt/storage\",\n  \"timeout\": null,\n  \"chunks\": {\n    \"size\": 1024,\n    \"number\": null\n  }\n}")

      whenReady(wsClient.url(testURL).post(body), Timeout(1 second)) { response =>
        println(response.body)
        response.status mustBe OK
      }
    }
  }
}
