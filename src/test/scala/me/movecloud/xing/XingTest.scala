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
    println("Xing test start")
  }
  
  after {
    println("Xing test end")
  }
  
  "Get Conferences infomation" should "not have exception" in {
    // 由于数据库不确定，所以不做检查
    
    val f1 = xing.getAllConferences(1)
    //f1.foreach(println(_))
    
    val f2 = xing.getConference(1)
    //f2.foreach(println(_))
    
    val f3 = xing.getConfTopics(1)
    //f3.foreach(println(_))
    
    val f4 = xing.getConfCity(1)
    //f4.foreach(println(_))
    
    val f5 = xing.getConfComments(1)
    //f5.foreach(println(_))
  }
}