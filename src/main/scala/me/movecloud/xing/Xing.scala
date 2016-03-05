package me.movecloud.xing

import models._
import exceptions._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import net.liftweb.json._
import com.ning.http.client._

import java.text.SimpleDateFormat
import java.util.Locale

class Xing {
  implicit val formats = new DefaultFormats {
    //Wed, 10 Feb 2016 00:00:00 GMT
    // EEE, dd MMM yyyy HH:mm:ss z
    override def dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", new Locale("en","US"))
  }

  def client = AsyncWebClient
  
  def urlPrefix = "http://xing.movecloud.me/api/v0.1/"
  
  private def consListHelper[T](key: String): String => List[T] = jsonStr => {
    val json = parse(jsonStr)
      
      val JArray(fj) = (json \ key)
      for {
        jp <- fj
      } yield jp.extract[T]
  }

  // 住构建器中进行全局设计
  
  // 请求客户端相关的设置
  // 时间限制等。。。

  // conference

  def getAllConferences(page: Int): Future[List[Conference]] = {
    val url = s"${urlPrefix}conferences?page=${page}"
    /**
    def helperParse(jsonStr: String): List[Conference] = {
      val json = parse(jsonStr)
      
      val JArray(fj) = (json \ "conferences")
      for {
        jp <- fj
      } yield jp.extract[Conference]
    }
    */
    client.get(url).map(consListHelper[Conference]("conferences"))
  }
  
  def getConference(conferenceId: Int): Future[Conference] = {
    val url = s"${urlPrefix}conferences/${conferenceId}"
    val jsonFuture = client.get(url).map(parse(_))
    
    jsonFuture.map(_.extract[Conference])
  }
  
  def getConfTopics(conferenceId: Int): Future[List[Topic]] = {
    getConference(conferenceId).flatMap(_.getTopics)
  }
  
  def getConfCity(conferenceId: Int): Future[City] = {
    getConference(conferenceId).flatMap(_.getCity)
  }
  
  def getConfComments(conferenceId: Int): Future[List[Comment]] = {
    getConference(conferenceId).flatMap(_.getComments)
  }
  
  
  // user
  
  def getUser(userId: Int): Future[User] = {
    val url = s"${urlPrefix}users/${userId}"
    val jsonFuture = client.get(url).map(parse(_))
    
    jsonFuture.map(_.extract[User])
  }
  
  def getUserConfes(userId: Int): Future[List[Conference]] = {
    val url = s"${urlPrefix}users/${userId}/conferences"
    def helper(str: String): List[Conference] = {
      val json = parse(str)
      
      val JArray(fj) = (json \ "conferences")
      
      for {
        jp <- fj
      } yield jp.extract[Conference]
    }
    
    client.get(url).map(helper)
  }
  
  def getUserFollowed(userId: Int): Future[List[User]] = {
    val url = s"${urlPrefix}users/${userId}/followed"
    def helper(str: String): List[Conference] = {
      val json = parse(str)
      
      val JArray(fj) = (json \ "conferences")
      
      for {
        jp <- fj
      } yield jp.extract[Conference]
    }
    
    client.get(url).map(helper)
  }
  
  def getUserFollowers(userId: Int): Future[List[User]] = ???
  
  
  // comment
  def getAllComments(page: Int): Future[List[Comment]] = ???
  
  
  // login
  def login(email: String, password: String): Future[String] = client.login(email, password)
  
  def getConferenceAttendees(isLogin: Future[String], conferenceId: Int): Future[List[User]] = {
    val url = "http://xing.movecloud.me/api/v0.1/conferences/${conferenceId}/attendees"
    
    val jsonFuture = client.tokenGet(isLogin, url)
    
    def helperParse(jsonStr: String): List[User] = {
      val json = parse(jsonStr)
      
      val JArray(fj) = (json \ "attendees")
      for {
        jp <- fj
      } yield jp.extract[User]
    }
    jsonFuture.map(helperParse(_))
  }
  
  def newConference(isLogin: Future[String], conference: Conference): Future[Conference] = ???
  
}


object Xing {

  
}