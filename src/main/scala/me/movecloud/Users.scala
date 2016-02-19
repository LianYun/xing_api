package me.movecloud

import net.liftweb.json._

class User(val id: Int) {
  def getInfo(): JValue = {
    JNothing
  }
}

object User {
  // 思考，如何组织呢？
  
  val oneUserUrl = "/users/<int:id>"
  
}