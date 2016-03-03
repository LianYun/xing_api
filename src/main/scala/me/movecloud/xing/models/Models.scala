package me.movecloud.xing
package models

import java.util.Date

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import net.liftweb.json._
import net.liftweb.json.JsonDSL._

import me.movecloud.xing.{AsyncWebClient=>client}



import me.movecloud.xing.utils._

case class Conference (
  /**id: Int,  暂且注释，待后端修改后添加*/
  title: String,
  description: String,
  start_time: Date,
  end_time: Date,
  time_stamp: Date,
  max_attendees: Int,
  city: String,
  topics: String,
  attendees: String,
  attendees_count: Int,
  comments: String,
  comments_count: Int
) {
    /**
    http://xing.movecloud.me/api/v0.1/conferences/2
    返回的JSON格式
    {
        "attendees": "http://xing.movecloud.me/api/v0.1/conferences/2/attendees", 
        "attendees_count": 15, 
        "city": "http://xing.movecloud.me/api/v0.1/conferences/3/city", 
        "comments": "http://xing.movecloud.me/api/v0.1/conferences/2/comments", 
        "comments_count": 3, 
        "description": "GHSg3OlA2HESRdPf2ahNlLD3zaFL6WGrrGFoVcm", 
        "end_time": "Wed, 10 Feb 2016 00:00:00 GMT", 
        "max_attendees": 16, 
        "start_time": "Tue, 09 Feb 2016 00:00:00 GMT", 
        "time_stamp": "Wed, 17 Feb 2016 00:00:00 GMT", 
        "title": "Integer eget nulla nunc?", 
        "topics": "http://xing.movecloud.me/api/v0.1/conferences/2/topics", 
        "url": "http://xing.movecloud.me/api/v0.1/conferences/2"
    }
    */
    
  def getCity(): Future[City] = {
    def helperParse(jsonStr: String): City = {
      val json = parse(jsonStr)
      json.extract[City]
    }
    client.get(city).map(helperParse(_))
  }
    
  def getTopics(): Future[List[Topic]] = {
    def helperParse(jsonStr: String): List[Topic] = {
      val json = parse(jsonStr)
      
      val JArray(fj) = (json \ "topics")
      for {
        jp <- fj
      } yield jp.extract[Topic]
    }
    client.get(topics).map(helperParse(_))
  }
  def getComments(): Future[List[Comment]] = {
    def helperParse(jsonStr: String): List[Comment] = {
      val json = parse(jsonStr)
      
      val JArray(fj) = (json \ "comments");
      for {
        jp <- fj
      } yield jp.extract[Comment]
    }
    client.get(comments).map(helperParse(_))
  }
    
  def getAttendees(name: String, passwd: String): Future[List[User]] = {
    
    val isLogin = client.login(name, passwd)
    
    def helperParse(jsonStr: String): List[User] = {
      val json = parse(jsonStr)
      
      val JArray(fj) = (json \ "attendees")
      for {
        jp <- fj
      } yield jp.extract[User]
    }
    client.tokenGet(isLogin, attendees).map(helperParse(_))
  }

}

case class User (
  /**id: Int,  暂且注释，待后端修改后添加*/
  nickname: String,
  about_me: String,
  address: String,
  email: String,
  url: String,
  confirmed: Boolean,
  conferences: String,
  conferences_count: Int,
  join_time: Date,
  last_seen: Date,
  portrait_addr: String
) {
    
    /**
    http://xing.movecloud.me/api/v0.1/users/2
    
    {
        "about_me": "Bulgaria Leva", 
        "address": "730 Heffernan Junction", 
        "conferences": "http://xing.movecloud.me/api/v0.1/users/2/conferences", 
        "conferences_count": 1, 
        "confirmed": true, 
        "email": "steve@aimbu.mil", 
        "join_time": "Thu, 03 Mar 2016 00:00:00 GMT", 
        "last_seen": "Tue, 08 Mar 2016 00:00:00 GMT", 
        "nickname": "Catherine Butler", 
        "portrait_addr": "http://www.ttoou.com/qqtouxiang/allimg/120918/co12091Q01643-6-lp.jpg", 
        "url": "http://xing.movecloud.me/api/v0.1/users/2"
    }
    */
  def getUserConfes(page: Int): Future[List[Conference]] = {
    val url = s"${conferences}?page=${page}"
    println(url)
    
    def helperParse(jsonStr: String): List[Conference] = {
      val json = parse(jsonStr)
      
      val JArray(fj) = (json \ "conferences")
      for {
        jp <- fj
      } yield jp.extract[Conference]
    }
    client.get(conferences).map(helperParse(_))
  }
    
  def getPortraitAddr(): Future[Nothing] = ???      // 获得头像的二进制数据
    
}
  
case class Comment (
  id: Int,
  author: String,
  body: String,
  conference: String,
  time_stamp: Date
) {
    /**
    http://xing.movecloud.me/api/v0.1/comments/1
    
    {
        "author": "http://xing.movecloud.me/api/v0.1/users/33", 
        "body": "fJfa6O0X8Dyp fcK", 
        "conference": "http://xing.movecloud.me/api/v0.1/conferences?id=70", 
        "id": 1, 
        "time_stamp": "Fri, 04 Mar 2016 00:00:00 GMT"
    }
    */
  def getAuthor(): Future[User] = ???
    
  def getConference(): Future[Conference] = ???
    
}
  
case class City (
  id: Int,
  name: String
)
  
case class Topic (
  id: Int,
  name: String
)