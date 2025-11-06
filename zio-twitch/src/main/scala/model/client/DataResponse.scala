package io.sommers.zio.twitch
package model.client

case class DataResponse[D](
  data: List[D]
)


