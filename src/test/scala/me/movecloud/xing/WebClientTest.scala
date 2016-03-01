package me.movecloud.xing

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


import net.liftweb.json._
import com.ning.http.client._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
  }
  
  test("get conferences info test") {
    //AsyncWebClient get prefix + "/conferences/1" map println
  }
  
  test("get comments info test") {
    //AsyncWebClient get prefix + "/comments/1" map println
  }
  
  test("authentation info test") {
    val fau = AsyncWebClient.login("lianyun08@126.com", "12")    
    val loginTestUrl = "http://xing.movecloud.me/api/v0.1/conferences/1/attendees"
    
    val fad = AsyncWebClient.tokenGet(fau, loginTestUrl)
    
    fad.foreach(println(_))
  }
}