package me.movecloud.xing

import java.text.SimpleDateFormat
import java.util.Locale

import net.liftweb.json._

object utils {
  val mainUrl = "http://xing.movecloud.me"
  
  val apiPrefix_0_1 = "/api/v0.1"
  
  def urlPrefix = s"${mainUrl}${apiPrefix_0_1}"
  
  implicit val formats = new DefaultFormats {
    //Wed, 10 Feb 2016 00:00:00 GMT
    // EEE, dd MMM yyyy HH:mm:ss z
    override def dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", new Locale("en","US"))
  }
  
  def consListHelper[T: Manifest](key: String): String => List[T] = jsonStr => {
    val json = parse(jsonStr)
      
      val JArray(fj) = (json \ key)
      for {
        jp <- fj
      } yield jp.extract[T]
  }
  
  def client = AsyncWebClient
}