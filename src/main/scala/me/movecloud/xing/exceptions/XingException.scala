package me.movecloud.xing
package exceptions

case class ExpireException(message: String) extends Exception(message)

case class WebsiteException(val code: Int, message: String) extends Exception(message)