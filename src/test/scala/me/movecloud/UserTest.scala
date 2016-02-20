
package me.movecloud

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import scala.util.{Try, Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class UserTest extends FunSuite {
  /**
  test("xxx") {        
    assert(true)
    intercept[Exception] { 
      //code 
    }
  }
  */
  
  test("Url config test") {
    import User._
    
    assert(oneUserUrl(1) == "http://xing.movecloud.me/api/v0.1/users/1")
  }
  
  test("info get test") {
    
    val res = User.getUser(1)
    val resValid = User.UserInfo("ly","bjliany@163.com","http://xing.movecloud.me/api/v0.1/users/1/conferences",1)
    assert(Await.result(res, Duration.Inf) == resValid)
  }
}