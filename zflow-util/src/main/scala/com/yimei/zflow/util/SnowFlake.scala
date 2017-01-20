package com.yimei.zflow.util

/**
  * https://mp.weixin.qq.com/s?__biz=MjM5ODYxMDA5OQ==&mid=403837240&idx=1&sn=ae9f2bf0cc5b0f68f9a2213485313127&key=a226a081696afed045bbfd3d3e3e39be9215c464f525017a6f44acc9a7f40c386d9b4173528653df7459032b87ffd9cc0ab77ac83fa8fef28969efb84165b81b5edcf7292bdcec2d7c67a5f102152b17&ascene=0&uin=MTY4ODU5NzEwNQ%3D%3D&devicetype=iMac+MacBookPro11%2C1+OSX+OSX+10.11.6+build(15G1212)&version=12010210&nettype=WIFI&fontScale=100&pass_ticket=vLut4ZkChbmG8gCGtj56okW9ZGpMrke6rFbjEPZoMwuoZ7SuHpxzhBREYpBABjG%2F
  *
  * @param mbit   milli second bits     毫秒数占的bits:   从系统上线那天起到当前的毫秒数: 比如把时间设置在20170101
  * @param bbit   business line bits    业务线
  * @param dbit   datacenter bits       数据中心
  * @param hbit   host bits             主机编号
  * @param sbit   serial bits           毫秒内序列号
  */
class SnowFlake(mbit: Int, bbit: Int, dbit: Int, hbit: Int, sbit: Int) {

  require(mbit + bbit + dbit + hbit + sbit < 64, "Invalid arguments")

  private[this] val startms = ???   // 开始时间

  // id生成器
  def nextId: Long = ???
}

object SnowFlake {
  def apply(mbit: Int, bbit: Int, dbit: Int, hbit: Int, sbit: Int) = new SnowFlake(mbit, bbit, dbit, hbit, sbit)

  /**
    * 假设系统需要运行10年:   20*365*24*3600*1000 = log(2, 20*365*24*3600*1000) =~ 39
    * 假设系统需要运行50年:   20*365*24*3600*1000 = log(2, 20*365*24*3600*1000) =~ 42
    *
    * @return
    */
  def apply() = new SnowFlake(39, 4, 2, 7, 7)
}
