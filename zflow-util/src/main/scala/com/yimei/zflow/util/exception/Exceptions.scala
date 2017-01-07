package com.yimei.zflow.util.exception

/**
  * Created by wangqi on 16/12/16.
  */
case class DatabaseException(message:String) extends Exception
case class ParameterException(message: String) extends Exception
case class BusinessException(message: String) extends Exception
