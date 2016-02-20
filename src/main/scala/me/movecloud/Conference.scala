package me.movecloud


import java.util.Date
import net.liftweb.json._

object Conference {

  def client

  case class City(
    id: Int,
    name: String
  }
  
  case class Topic (
    id: Int,
    name: String
  )
  
  
  case class ConfInfo (
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
  
  }
  
}