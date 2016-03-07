package me.movecloud.xing

import models._
import exceptions._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.reflect.ClassTag

import net.liftweb.json._
import com.ning.http.client._

import java.text.SimpleDateFormat
import java.util.Locale

import me.movecloud.xing.utils._

class Xing {

  // 住构建器中进行全局设计
  
  // 请求客户端相关的设置
  // 时间限制等。。。

  // conference

  def getAllConferences(page: Int): Future[List[Conference]] = {
    val url = s"${urlPrefix}/conferences?page=${page}"
    client.get(url).map(consListHelper[Conference]("conferences"))
  }
  
  def getConference(conferenceId: Int): Future[Conference] = {
    val url = s"${urlPrefix}/conferences/${conferenceId}"
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
    val url = s"${urlPrefix}/users/${userId}"
    val jsonFuture = client.get(url).map(parse(_))
    jsonFuture.map(_.extract[User])
  }
  
  def getUserConfes(userId: Int): Future[List[Conference]] = {
    val url = s"${urlPrefix}/users/${userId}/conferences"    
    client.get(url).map(consListHelper[Conference]("conferences"))
  }
  
  def getUserFollowed(userId: Int): Future[List[User]] = {
    val url = s"${urlPrefix}/users/${userId}/followed"    
    client.get(url).map(consListHelper[User]("followed"))
  }
  
  def getUserFollowers(userId: Int): Future[List[User]] = {
    val url = s"${urlPrefix}/users/${userId}/followers"    
    client.get(url).map(consListHelper[User]("followers"))
  }
  
  
  // comment
  def getComment(commentId: Int): Future[Comment] = {
    val url = s"${urlPrefix}/comments/${commentId}"
    val jsonFuture = client.get(url).map(parse(_))
    jsonFuture.map(_.extract[Comment])
  }
  
  
  // login
  def login(email: String, password: String): Future[String] = client.login(email, password)
  
  def getConferenceAttendees(isLogin: Future[String], conferenceId: Int): Future[List[User]] = {
    val url = s"${urlPrefix}/conferences/${conferenceId}/attendees"
    
    val jsonFuture = client.tokenGet(isLogin, url)
    jsonFuture.map(consListHelper[User]("attendees"))
  }
  
  def newConference(isLogin: Future[String], conference: Conference): Future[Conference] = {
    /**
      {
        "title" : "...",
        "city" : "name",
        "description" : "...md...",
        "topics": ["name1", "name2"],
        "start_time" : "DATE",
        "end_time" : "DATE",
        "max_attendees": "<int:n>"
      }
    */
    ???
  }
  
}


object Xing {
  def apply(): Xing = new Xing
}