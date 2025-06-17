package io.sommers.aiintheipaw
package database

import com.augustnagro.magnum.SqlNameMapper

object CamelCaseNoEntitySqlNameMapper extends SqlNameMapper {
  override def toColumnName(scalaName: String): String = toCase(scalaName)

  override def toTableName(scalaName: String): String = toCase(scalaName).replace("_entity", "")

  private def toCase(scalaName: String): String =
    val res = StringBuilder().append(scalaName.head.toLower)
    for i <- 1 until scalaName.length do
      val c = scalaName.charAt(i)
      if c.isUpper then res.append('_').append(c.toLower)
      else res.append(c)
    res.result()
}
