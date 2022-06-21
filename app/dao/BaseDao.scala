package dao

import Commons.Logging
import model.Schema
import org.bson.codecs.configuration.CodecRegistries.{fromCodecs, fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.jsr310.LocalDateTimeCodec
import org.mongodb.scala.{MongoClient, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}

trait BaseDao extends Logging {

  protected def seqFromTO[T, D](
      convert: T => Future[D]
  )(tos: Seq[T])(implicit ec: ExecutionContext): Future[Seq[D]]=
    Future.sequence(tos.map(convert))

  private val codecRegistry: CodecRegistry = fromRegistries(
    fromProviders(
      classOf[Schema],
    ),
    fromCodecs(new LocalDateTimeCodec()),
    MongoClient.DEFAULT_CODEC_REGISTRY
  )

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("testdb").withCodecRegistry(codecRegistry)

}
