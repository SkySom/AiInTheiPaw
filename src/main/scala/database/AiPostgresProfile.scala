package io.sommers.aiintheipaw
package database

import model.service.Service
import model.sprint.SprintStatus

import com.github.tminglei.slickpg.{ExPostgresProfile, PgDate2Support, PgEnumSupport}
import slick.basic.Capability
import slick.jdbc.JdbcType
import zio.{Duration, duration2DurationOps}

trait AiPostgresProfile extends ExPostgresProfile with PgEnumSupport with PgDate2Support {
  override protected def computeCapabilities: Set[Capability] = super.computeCapabilities + slick.jdbc.JdbcCapabilities.insertOrUpdate

  override val api: AiPostgresApi = new AiPostgresApi {}

  trait AiPostgresApi extends ExtPostgresAPI
    with ColumnImplicits
    with Date2DateTimeImplicitsDuration

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
  }
}

object AiPostgresProfile extends AiPostgresProfile


