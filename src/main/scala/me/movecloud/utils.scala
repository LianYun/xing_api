package me.movecloud.xing

import java.text.SimpleDateFormat
import java.util.Locale

import net.liftweb.json._

object utils {
  val mainUrl = "http://xing.movecloud.me"
  
  val apiPrefix_0_1 = "/api/v0.1"
  
  implicit val formats = new DefaultFormats {
    //Wed, 10 Feb 2016 00:00:00 GMT
    // EEE, dd MMM yyyy HH:mm:ss z
    override def dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", new Locale("en","US"))
  }
}