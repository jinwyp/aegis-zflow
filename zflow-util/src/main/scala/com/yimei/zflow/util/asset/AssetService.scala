package com.yimei.zflow.util.asset

import com.yimei.zflow.util.asset.db.AssetTable
import com.yimei.zflow.util.asset.db.Entities.AssetEntity

import scala.concurrent.Future

/**
  * Created by wangqi on 17/1/6.
  */
trait AssetService extends AssetTable {

  import driver.api._

  def getFiles(fileIds:List[String]): Future[Seq[AssetEntity]] = {

    dbrun((for(
      f <- assetClass if f.asset_id inSetBind(fileIds)
    ) yield {
      f
    }).result)

  }
}
