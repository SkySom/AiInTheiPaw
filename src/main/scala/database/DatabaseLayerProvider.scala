package io.sommers.aiintheipaw
package database

import com.augustnagro.magnum.magzio.TransactorZIO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import zio.config.magnolia.deriveConfig
import zio.{Config, ZIO, ZLayer}

import java.util.Properties
import javax.sql.DataSource

case class DataSourceConfig(
  dataSourceClassName: String,
  user: String,
  password: String,
  databaseName: String,
  portNumber: Int,
  serverUrl: String
) {
  def toHikari: HikariConfig = {
    val properties: Properties = Properties(5)
    properties.setProperty("dataSourceClassName", dataSourceClassName)
    properties.setProperty("dataSource.user", user)
    properties.setProperty("dataSource.password", password)
    properties.setProperty("dataSource.databaseName", databaseName)
    properties.put("dataSource.portNumber", portNumber)
    HikariConfig(properties)
  }
}

object DataSourceConfig {
  val config: Config[DataSourceConfig] = deriveConfig[DataSourceConfig]
}

object DataSourceProvider {
  val live: ZLayer[Any, Config.Error, DataSource] = ZLayer.scoped {
    ZIO.acquireRelease {
      for {
        config <- ZIO.configProviderWith(_.nested("database")
          .load(DataSourceConfig.config)
        )
      } yield HikariDataSource(config.toHikari)
    } {
      dataSource => ZIO.succeed(dataSource.close())
    }
  }

  val transactorLive: ZLayer[Any, Config.Error, TransactorZIO] = DataSourceProvider.live >>> TransactorZIO.layer
}