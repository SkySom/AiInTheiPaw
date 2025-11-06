package io.sommers.zio.slick

import com.typesafe.config.{ConfigFactory, Config as TypeSafeConfig}
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile
import slick.sql.FixedSqlStreamingAction
import zio.config.*
import zio.config.magnolia.*
import zio.{Config, Task, UIO, ZIO, ZLayer}

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters.*

trait DatabaseZIO {
  val profile: JdbcProfile

  val database: Database

  def run[R](action: ExecutionContext => DBIOAction[R, NoStream, Nothing]): Task[R]

  def run[R](action: DBIOAction[R, NoStream, Nothing]): Task[R]

  def runStream[R, T, E <: Effect](action: profile.StreamingProfileAction[R, T, E]): Task[R]
}

object DatabaseZIO {
  def live(path: String, profile: JdbcProfile): ZLayer[Any, Throwable, DatabaseZIO] = database(Some(path)) >>> ZLayer.fromFunction(DatabaseZIOLive(_, profile))

  def live(profile: JdbcProfile): ZLayer[Any, Throwable, DatabaseZIO] = database(None) >>> ZLayer.fromFunction(DatabaseZIOLive(_, profile))

  private def database(path: Option[String]): ZLayer[Any, Throwable, Database] = ZLayer.scoped {
    for {
      config <- ZIO.configProviderWith(provider => path.fold(provider.load[DatabaseConfig])(provider.nested(_).load[DatabaseConfig]))
      writtenConfig <- writeToConfigMap(config)
      database <- ZIO.acquireRelease(ZIO.attempt(Database.forConfig("", writtenConfig)))(db => ZIO.succeed(db.close))
    } yield database
  }

  private def writeToConfigMap(config: DatabaseConfig): UIO[TypeSafeConfig] = {
    val configMap: mutable.Map[String, Any] = new mutable.HashMap[String, Any]()
    config.connectionPool.foreach(configMap.put("connectionPool", _))
    config.numThreads.foreach(configMap.put("numThreads", _))
    configMap.put("dataSourceClass", config.dataSourceClass)
    configMap.put("properties", config.properties.asJava)

    ZIO.succeed(ConfigFactory.parseMap(configMap.asJava))
  }
}

case class DatabaseZIOLive(
  override val database: Database,
  override val profile: JdbcProfile
) extends DatabaseZIO {
  override def run[R](action: DBIOAction[R, NoStream, Nothing]): Task[R] = {
    ZIO.fromFuture(_ => database.run(action))
  }

  override def run[R](action: ExecutionContext => DBIOAction[R, NoStream, Nothing]): Task[R] = {
    ZIO.fromFuture(ec => database.run(action(ec)))
  }

  override def runStream[R, T, E <: Effect](action: FixedSqlStreamingAction[R, T, E]): Task[R] = {
    ZIO.fromFuture(_ => database.run(action))
  }
}

case class DatabaseConfig(
  connectionPool: Option[String],
  dataSourceClass: String,
  properties: Map[String, String],
  numThreads: Option[Int]
) derives Config
