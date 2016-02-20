package me.movecloud

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner




import net.liftweb.json._

@RunWith(classOf[JUnitRunner])
class WebClientTest extends FunSuite {
  import WebsiteConfig._
  import API0_1._
  val prefix = mainUrl + apiPrefix
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
  }
  
  test("get conferences info test") {
    //AsyncWebClient get prefix + "/conferences/1" map println
  }
  
  test("get comments info test") {
    //AsyncWebClient get prefix + "/comments/1" map println
  }
  
  test("authentation info test") {
  
  }
}