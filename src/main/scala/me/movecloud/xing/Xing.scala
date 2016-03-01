package me.movecloud.xing

import models._
import exceptions._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Xing {

  // 住构建器中进行全局设计
  
  // 请求客户端相关的设置
  // 时间限制等。。。

  // conference

  def getAllConferences(page: Int): Future[List[Conference]] = ???
  
  def getConference(conferenceId: Int): Future[Conference] = ???
  
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
  
  def getUser(userId: Int): Future[User] = ???
  
  def getUserConfes(userId: Int): Future[List[Conference]] = ???
  
  def getUserFollowed(userId: Int): Future[List[User]] = ???
  
  def getUserFollowers(userId: Int): Future[List[User]] = ???
  
  
  // comment
  def getAllComments(page: Int): Future[List[Comment]] = ???
  
  
  // login
  def login(email: String, password: String): Future[String] = ???
  
  def getConferenceAttendees(isLogin: Future[String], conferenceId: Int): Future[List[User]] = ???
  
  def newConference(isLogin: Future[String], conference: Conference): Future[Conference] = ???
  
}


object Xing {

  
}