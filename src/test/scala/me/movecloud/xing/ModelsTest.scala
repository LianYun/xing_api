package me.movecloud.xing

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class ModelsTest extends FlatSpec {
  /**
  test("xxx") {        
    assert(true)
    intercept[Exception] { 
      //code 
    }
  }
  */
  
  val xing = Xing()
  
  
  "Conferences Model" should "not have exception" in {
    val c1 = xing.getConference(1)
    
    val f1 = c1.flatMap(_.getCity())
    Await.result(f1, Duration.Inf)
    //f1.foreach(println(_))
    
    val f2 = c1.flatMap(_.getTopics())
    Await.result(f2, Duration.Inf)
    //f2.foreach(println(_))
    
    val f3 = c1.flatMap(_.getComments())
    Await.result(f3, Duration.Inf)
    //f3.foreach(println(_))
    
    val f4 = c1.flatMap(_.getAttendees("lianyun08@126.com", "12"))
    Await.result(f4, Duration.Inf)
    //f4.foreach(println(_))
  }
  
  "User model" should "not have exception" in {
    val u1 = xing.getUser(1)
    
    val f1 = u1.flatMap(_.getUserConfes(1))
    Await.result(f1, Duration.Inf)
    // f1.foreach(println(_))
  }
  
  "Comment model" should "not have exception" in {
    val co1 = xing.getComment(1)
    
    val f1 = co1.flatMap(_.getAuthor())
    Await.result(f1, Duration.Inf)
    f1.foreach(println(_))
    
    val f2 = co1.flatMap(_.getConference())
    Await.result(f2, Duration.Inf)
    f2.foreach(println(_))
    
  }
}