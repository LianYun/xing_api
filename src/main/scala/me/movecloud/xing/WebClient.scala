package me.movecloud.xing

import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.ExecutionContext


import java.util.concurrent.Executor

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.Realm

import net.liftweb.json._
import net.liftweb.json.JsonDSL._

trait WebClient {
  def get(url: String)(implicit exec: Executor): Future[String]
}

case class BadStatus(status: Int) extends RuntimeException

object AsyncWebClient extends WebClient{
  

  private val client = new AsyncHttpClient

  def get(url: String)(implicit exec: Executor): Future[String] = {
    val f = client.prepareGet(url).execute();
    val p = Promise[String]()
    f.addListener(new Runnable {
      def run = {
        val response = f.get
        if (response.getStatusCode / 100 < 4)
          p.success(response.getResponseBodyExcerpt(131072))
        else p.failure(BadStatus(response.getStatusCode))
                }
    }, exec)
    p.future
  }
  
  def login(name: String, password: String)(implicit exec: Executor): Future[String] = {
    val tokenUrl = "http://xing.movecloud.me/api/v0.1/token"
    val realm: Realm = new Realm.RealmBuilder()
               .setPrincipal(name)
               .setPassword(password)
               .setUsePreemptiveAuth(true)
               .setScheme(Realm.AuthScheme.BASIC)
               .build();
    val f = client.prepareGet(tokenUrl).setRealm(realm).execute()
    val p = Promise[String]()
    
    f.addListener(new Runnable {
      def run = {
        val response = f.get
        if (response.getStatusCode / 100 < 4) {
          val body = response.getResponseBodyExcerpt(131072)
          val json = parse(body)
          p.success((json \ "token").values.toString)
        }
        else p.failure(BadStatus(response.getStatusCode))
      }
    }, exec)
    p.future
  }
  
  def tokenGet(tokenFuture: Future[String], url: String)(implicit exec: Executor): Future[String] = {
    implicit val exectext = ExecutionContext.fromExecutor(exec)
    tokenFuture.flatMap(token =>
        {
            println(token)
            val realm: Realm = new Realm.RealmBuilder()
                    .setPrincipal(token)
                    .setPassword("")
                    .setUsePreemptiveAuth(true)
                    .setScheme(Realm.AuthScheme.BASIC)
                    .build();
            val f = client.prepareGet(url).setRealm(realm).execute()
            val p = Promise[String]()
            
            f.addListener(new Runnable {
            def run = {
                val response = f.get
                if (response.getStatusCode / 100 < 4) {
                val body = response.getResponseBodyExcerpt(131072)
                p.success(body)
                }
                else p.failure(BadStatus(response.getStatusCode))
            }
            }, exec)
            p.future
        })
  }

  def shutdown(): Unit = client.close()

}
