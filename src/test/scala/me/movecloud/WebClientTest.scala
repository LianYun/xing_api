package me.movecloud

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import API0_1._
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
  */
  
  test("get user info test") {
    AsyncWebClient get prefix + "/users/1" map println
  }
  
  test("get conferences info test") {
    AsyncWebClient get prefix + "/conferences/1" map println
  }
  
  test("get comments info test") {
    AsyncWebClient get prefix + "/comments/1" map println
  }
  
  test("authentation info test") {
  
  }
}