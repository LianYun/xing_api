package me.movecloud.xing

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import com.ning.http.client._

import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RunWith(classOf[JUnitRunner])
class XingTest extends FlatSpec  with BeforeAndAfter {
  /**
  "Testing" should "xxx" in {        
    assert(true)
    intercept[Exception] { 
      //code 
    }
  }
  
  it should "xxx" in {
   asser(true)
   intercept[XxxException] {
    xxxx
   }
  }
  */
  
  val xing = new Xing()
  
  before {
    //println("Xing test start")
  }
  
  after {
    //println("Xing test end")
  }
  
  "Get Conferences infomation" should "not have exception" in {
    // 由于数据库不确定，所以不做检查
    
    val f1 = xing.getAllConferences(1)
    Await.result(f1, Duration.Inf)
    //f1.foreach(println(_))
    
    val f2 = xing.getConference(1)
    Await.result(f2, Duration.Inf)
    //f2.foreach(println(_))
    
    val f3 = xing.getConfTopics(1)
    Await.result(f3, Duration.Inf)
    //f3.foreach(println(_))
    
    val f4 = xing.getConfCity(1)
    Await.result(f4, Duration.Inf)
    //f4.foreach(println(_))
    
    val f5 = xing.getConfComments(1)
    Await.result(f5, Duration.Inf)
    //f5.foreach(println(_))
  }
  
  "Get User Infomation" should "not have exception" in {
    val f1 = xing.getUser(1)
    Await.result(f1, Duration.Inf)
    //f1.foreach(println(_))
    
    val f2 = xing.getUserConfes(1)
    Await.result(f2, Duration.Inf)
    //f2.foreach(_.foreach(println(_)))
    
    val f3 = xing.getUserFollowed(1)
    Await.result(f3, Duration.Inf)
    //f3.foreach(_.foreach(println(_)))
    
    val f4 = xing.getUserFollowers(1)
    Await.result(f4, Duration.Inf)
    //f4.foreach(_.foreach(println(_)))
  }
  
  "Get Comments Infomation" should "not have exception" in {
    val f1 = xing.getComment(1)
    Await.result(f1, Duration.Inf)
    //f1.foreach(println(_))
  }
  
  "New Conference Test" should "not hava exception" in {
    val json = 
      ("title" -> "json post test") ~
      ("city" -> "xian") ~
      ("description" -> "") ~
      ("topics" -> "") ~
      ("start_time" -> "") ~
      ("end_time" -> "") ~
      ("max_attendees" -> 1)
      
    println(pretty(render(json)))
  }
}