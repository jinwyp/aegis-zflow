package com.yimei.zflow.util.asset.db

import java.sql.Timestamp

import com.yimei.zflow.util.DB
import com.yimei.zflow.util.asset.db.Entities.AssetEntity

/**
  * Created by hary on 16/12/31.
  */
trait AssetTable extends DB {

  import driver.api._

  class AssetClass(tag: Tag) extends Table[AssetEntity](tag, "asset") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

    def asset_id = column[String]("asset_id")

    def file_type = column[String]("file_type")

    def busi_type = column[String]("busi_type")

    def username = column[String]("username")

    def description = column[Option[String]]("description")

    def url = column[String]("url")

    def origin_name = column[String]("origin_name")

    def ts_c = column[Option[Timestamp]]("ts_c")

    def * = (id, asset_id, file_type, busi_type, username, description, url, origin_name, ts_c) <>(AssetEntity.tupled, AssetEntity.unapply)
  }

  protected val assetClass = TableQuery[AssetClass]

}
