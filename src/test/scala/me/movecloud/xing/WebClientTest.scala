package me.movecloud.xing

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


import net.liftweb.json._
import com.ning.http.client._

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(classOf[JUnitRunner])
class WebClientTest extends FunSuite {
  /**
  test("xxx") {        
    assert(true)
    intercept[Exception] { 
      //code 
    }
  }
  
  http://xing.movecloud.me/api/v0.1/users/1
  
  
  */
  
  test("get user info test") {
    val userUrl = "http://xing.movecloud.me/api/v0.1/users/1"
    val xing =  new Xing()
    
    val userFuture = xing.getUser(1)
    
    //userFuture.foreach(println(_))
    
    
  }
  
  test("get conferences info test") {
    implicit val formats = DefaultFormats
    //AsyncWebClient get prefix + "/conferences/1" map println
    val jf = AsyncWebClient.get("http://xing.movecloud.me/api/v0.1/conferences/2/topics")
    import models.Topic
    
    jf.foreach{ str =>
      val json = parse(str)
      
      val JArray(fj) = (json \ "topics");
      val topics = for {
        jp <- fj
      } yield jp.extract[Topic]
      assert(topics == List(Topic(4,"Movie"), Topic(3,"Programming"), Topic(1,"Web")))
    }
    
    val xing = new Xing()
    
    val fc = xing.getConference(2)
    //println(Await.result(fc, Duration.Inf))
    val city = fc.flatMap(_.getCity())
    //println(Await.result(city, Duration.Inf))
    //println(Await.result(fc.flatMap(_.getTopics()), Duration.Inf))
    
    val fcs = xing.getAllConferences(2)
    //for (c <- Await.result(fcs, Duration.Inf))
      //println(c)
    
    //val format: SimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", new Locale("en","US"))
    //val parsed: Date = format.parse("Wed, 10 Feb 2016 00:00:00 PDT")
    //println(parsed)
  }
  
  test("get comments info test") {
    //AsyncWebClient get prefix + "/comments/1" map println
    val xing = new Xing()
    val fc = xing.getConference(2)
    //fc.flatMap(_.getComments).foreach(println(_))
    
    //fc.flatMap(_.getAttendees("lianyun08@126.com", "12")).foreach(println(_))
  }
  
  test("authentation info test") {
    val fau = AsyncWebClient.login("lianyun08@126.com", "12")    
    val loginTestUrl = "http://xing.movecloud.me/api/v0.1/conferences/1/attendees"
    
    val fad = AsyncWebClient.tokenGet(fau, loginTestUrl)
    
    //println(Await.result(fad, Duration.Inf))
  }
}