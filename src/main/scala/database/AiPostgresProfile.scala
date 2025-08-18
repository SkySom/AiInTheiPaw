package io.sommers.aiintheipaw
package database

import model.service.Service
import model.sprint.SprintStatus

import com.github.tminglei.slickpg.ExPostgresProfile.ExtPostgresAPI
import com.github.tminglei.slickpg.date.PgDateExtensions
import com.github.tminglei.slickpg.enums.PgEnumExtensions
import com.github.tminglei.slickpg.{ExPostgresProfile, PgDate2Support, PgEnumSupport}
import slick.basic.Capability
import slick.jdbc.JdbcType
import zio.Duration

import java.time.Duration as JavaDuration

trait AiPostgresProfile extends ExPostgresProfile with PgEnumSupport with PgDate2Support {
  override protected def computeCapabilities: Set[Capability] = super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  override val api: AiPostgresApi = new AiPostgresApi {}

  trait AiPostgresApi extends ExtPostgresAPI
    with ColumnImplicits

  trait ColumnImplicits extends JdbcAPI {
    implicit val sprintStatusTypeMapper: JdbcType[SprintStatus] = createEnumJdbcType[SprintStatus](
      "sprint_status",
      _.toString,
      SprintStatus.valueOf,
      false
    )

    implicit val service: JdbcType[Service] = createEnumJdbcType[Service](
      "service",
      _.toString,
      Service.valueOf,
      false
    )

    implicit val duration: BaseColumnType[Duration] = MappedColumnType.base[Duration, JavaDuration](
      duration => duration,
      Duration.fromJava
    )
  }
}

object AiPostgresProfile extends AiPostgresProfile


