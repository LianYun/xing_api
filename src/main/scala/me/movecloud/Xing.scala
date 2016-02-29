
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

  import java.util.Date
  import net.liftweb.json._
  
  case class Conference (
    title: String,
    description: String,
    start_time: Date,
    end_time: Date,
    time_stamp: Date,
    max_attendees: Int,
    city: String,
    topics: String,
    attendees: String
  ) {
    /**
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
      client.get(city).map(helperParse)
    }
    
    def getTopics(): Future[List[Topic]] = {
      def helperParse(jsonStr: String): City = {
        val json = parse(jsonStr)
        json.extract[City]
      }
      client.get(city).map(helperParse)
    }
    def getComments(): Future[List[Comment]] = {
    
    }
    
    def getAttendees(): Future[List[User]] = ???
  
  }
  case class User (
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
    def getUserConfes(page: Int): Future[List[Conference]] = ???
    
    def getPortraitAddr(): Future[Image] = ???
    
  }
  case class Comment
  
  case class City {
    id: Int,
    name: String
  }
  
  case class Topic (
    id: Int,
    name: String
  )
}