package me.movecloud

import net.liftweb.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class User {
  import User._
}

object User {
  
  //def apply: User = new User
  // 思考，如何组织呢？
    
  case class UserInfo(
    nickname: String, 
    email: String, 
    conferences: String,
    conferences_count: Int
  )
  
  import WebsiteConfig._
  import API0_1._       // 使用0.1版本的API
  
  val seq = "/"
  
  val prefix = mainUrl + apiPrefix
  
  
  def client = AsyncWebClient
  
  def getUser(id: Int): Future[UserInfo] = {
    val url = s"${prefix}${seq}users${seq}$id"
    val jsonStr: Future[String] = client get url
    def parseUser(jsonStr: String): UserInfo = {
      implicit val formats = DefaultFormats
      val json = parse(jsonStr)
      json.extract[UserInfo]
    }
    jsonStr map parseUser
  }
  
  def getUserConferences(id: Int): 
}