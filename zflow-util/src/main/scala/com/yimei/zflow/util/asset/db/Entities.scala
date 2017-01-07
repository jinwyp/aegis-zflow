package com.yimei.zflow.util.asset.db

import java.sql.Timestamp


object Entities {

  /**
    * Created by hary on 17/1/6.
    */
  case class AssetEntity(id: Option[Long],
                         asset_id: String,
                         file_type: Int,
                         busi_type: String,
                         username: String,
                         gid: Option[String],
                         description: Option[String],
                         url: String,
                         origin_name: String,
                         ts_c: Option[Timestamp])



}
