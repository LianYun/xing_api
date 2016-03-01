
import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.{read, write}
import net.liftweb.json.Xml.{toJson, toXml}
//import net.liftweb.json.JsonParser._

import java.util.Date

@RunWith(classOf[JUnitRunner])
class LiftJsonTest extends FunSuite {
  /**
  test("xxx") {        
    assert(true)
    intercept[Exception] { 
      //code 
    }
  }
  */
  
  test("simple fromJson Test") {
    implicit val formats = DefaultFormats
    case class Child(name: String, age: Int, birthdate: Option[Date])
    case class Address(street: String, city: String)
    case class Person(name: String, address: Address, children: List[Child])
    val json = parse("""
         { "name": "joe",
           "address": {
             "street": "Bulevard",
             "city": "Helsinki"
           },
           "children": [
             {
               "name": "Mary",
               "age": 5
               "birthdate": "2004-09-04T18:06:22Z"
             },
             {
               "name": "Mazy",
               "age": 3
             }
           ]
         }
       """)
    val p = json.extract[Person]
    val d = new Date(2004-1900, 8, 5, 2, 6, 22)  // 注意时间的构建
    assert(p == Person("joe", Address("Bulevard","Helsinki"), List(Child("Mary",5,Some(d)), Child("Mazy",3,None))))
    
    val res = JObject(List(JField("numbers",JArray(List(JInt(1), JInt(2), JInt(3), JInt(4))))))
    assert(parse(""" { "numbers" : [1, 2, 3, 4] } """) == res)
  }
  
  test("simple toJson Test") {
    val jsonl = List(1, 2, 3, 4)
    assert(compact(render(jsonl)) == "[1,2,3,4]")
    
    val jsont = ("name" -> "joe")
    assert(compact(render(jsont)) == """{"name":"joe"}""")
    
    val jsonm = ("name" -> "joe") ~ ("age" -> 35)
    assert(compact(render(jsonm)) == """{"name":"joe","age":35}""")
    
    val jsonm2 = ("name" -> "joe") ~ ("age" -> Some(35))
    assert(compact(render(jsonm2)) == """{"name":"joe","age":35}""")
    
    val jsonm3 = ("name" -> "joe") ~ ("age" -> (None: Option[Int]))
    assert(compact(render(jsonm3)) == """{"name":"joe"}""")
  }
  
  test("json merge test") {
    val lotto1 = parse("""{
         "lotto":{
           "lotto-id":5,
           "winning-numbers":[2,45,34,23,7,5,3]
           "winners":[{
             "winner-id":23,
             "numbers":[2,45,34,23,3,5]
           }]
         }
       }""")
    val lotto2 = parse("""{
         "lotto":{ 
           "winners":[{
             "winner-id":54,
             "numbers":[52,3,12,11,18,22]
           }]
         }
       }""")
    val mergedLotto = lotto1 merge lotto2
    
    //println(pretty(render(mergedLotto)))
    
    val Diff(changed, added, deleted) = lotto2 diff lotto1
    
    val changedValid = JObject(List(JField("lotto",JObject(List(JField("winners",JObject(List(JField("winner-id",JInt(23)), JField("numbers",JArray(List(JInt(2), JInt(45), JInt(34), JInt(23), JInt(3), JInt(5))))))))))))
    val addedValid = JObject(List(JField("lotto",JObject(List(JField("lotto-id",JInt(5)), JField("winning-numbers",JArray(List(JInt(2), JInt(45), JInt(34), JInt(23), JInt(7), JInt(5), JInt(3)))))))))
    val deletedVal = JNothing
    
    assert(changed == changedValid)
    assert(added == addedValid)
    assert(deleted == deletedVal)
  }
  
  test("LINQ style query") {
    import net.liftweb.json._

    val json = parse("""
        { "name": "joe",
          "children": [
            {
              "name": "Mary",
              "age": 5
            },
            {
              "name": "Mazy",
              "age": 3
            }
          ]
        }
        """)
    //println(json)
    
    val ages = for { JField("age", JInt(age)) <- json } yield age
    assert(ages == List(5, 3))
    
    val names = for { JField("name", JString(name)) <- json} yield name
    
    assert(names == List("joe", "Mary", "Mazy"))
    
    val pairs = for {
      JObject(child) <- json
      JField("name", JString(name)) <- child
      JField("age", JInt(age)) <- child
      if age > 4
    } yield (name, age)
    
    assert(pairs == List(("Mary",5)))
  }
  
  test("XPath HOFs test") {
    val json =
     ("person" -> {
       ("name" -> "Joe") ~
       ("age" -> 35) ~
       ("spouse" -> {
         ("person" -> {
           ("name" -> "Marilyn") ~
           ("age" -> 33)
         })
       })
     })
    val res0 = json \\ "age"
    assert(res0 == JObject(List(JField("age",JInt(35)), JField("age",JInt(33)))))
    
    assert(compact(render(res0)) == """{"age":35,"age":33}""")
    
    val res1 = json \\ "name"
    assert(compact(render(res1)) == """{"name":"Joe","name":"Marilyn"}""")
    
    val res2 = json \ "person" \ "name"
    assert(compact(render(res2)) == "\"Joe\"")
    
    val res3 = json \ "person" \ "spouse" \ "person" \ "name"
    assert(compact(render(res3)) == "\"Marilyn\"")
    
    val jsonfl = json filter {
      case JField("name", _) => true
      case _ => false
    }
    assert(jsonfl == List(JField("name",JString("Joe")), JField("name",JString("Marilyn"))))
       
    val jsonfd = json find {
      case JField("name", _) => true
      case _ => false
    }
    assert(jsonfd == Some(JField("name",JString("Joe"))))
    
    val jsontf = json transform {
         case JField("name", JString(s)) => JField("NAME", JString(s.toUpperCase))
    }
    //println(jsontf)
    
    val jsonValue = json.values
    assert(jsonValue == Map("person" -> Map("name" -> "Joe", "age" -> 35, "spouse" -> Map("person" -> Map("name" -> "Marilyn", "age" -> 33)))))
    
    val json2 = parse("""
         { "name": "joe",
           "children": [
             {
               "name": "Mary",
               "age": 5
             },
             {
               "name": "Mazy",
               "age": 3
             }
           ]
         }
       """)
    assert((json2 \ "children")(0) == JObject(List(JField("name",JString("Mary")), JField("age",JInt(5)))))
    
    assert((json2 \ "children")(1) \ "name" == JString("Mazy"))
    
    val res4 = json2 \\ classOf[JInt]
    assert(res4 == List(5, 3))
    
    val res5 = json2 \ "children" \\ classOf[JString]
    assert(res5 == List("Mary", "Mazy"))
  }
  
  object example {
    implicit val formats = DefaultFormats // Brings in default date formats etc.
    case class Child(name: String, age: Int, birthdate: Option[java.util.Date])
    case class Address(street: String, city: String)
    case class Person(name: String, address: Address, children: List[Child])
    case class Bike(make: String, price: Int) {
      def this(price: Int) = this("Trek", price)
      override def equals(other: Any): Boolean = {
        if (other.isInstanceOf[Bike]) {
          val ob = other.asInstanceOf[Bike] 
            ob match {
                case b: Bike => b.make.equals(make) && b.price == price
                case _ =>  false
            }
        }
        else false
      }
    }
    
    case class PersonWithAddresses(name: String, addresses: Map[String, Address])
  }
  
  
  test("simple extracting values test") {
    import example._
    
    val json = parse("""
         { "name": "joe",
           "address": {
             "street": "Bulevard",
             "city": "Helsinki"
           },
           "children": [
             {
               "name": "Mary",
               "age": 5
               "birthdate": "2004-09-04T18:06:22Z"
             },
             {
               "name": "Mazy",
               "age": 3
             }
           ]
         }
       """)
     val p = json.extract[Person]
     val d = new Date(2004-1900, 8, 5, 2, 6, 22)  // 注意时间的构建
     
     //println(p.children(0).birthdate)
     val pValid = Person("joe", Address("Bulevard","Helsinki"), List(Child("Mary",5,Some(d)), Child("Mazy",3,None)))
     //println(pValid.children(0).birthdate)
     //assert(p === (pValid))
     
     //Person(joe,Address(Bulevard,Helsinki),List(Child(Mary,5,Some(Sun Sep 05 02:06:22 CST 2004)), Child(Mazy,3,None)))
     //Person(joe,Address(Bulevard,Helsinki),List(Child(Mary,5,Some(Sun Sep 05 02:06:22 CST 2004)), Child(Mazy,3,None)))
     
     val b1 = parse(""" {"price":350} """).extract[Bike]
     val b1Valid = Bike("Trek",350)
     assert(b1.make == b1Valid.make && b1.price == b1Valid.price)
     
     val b2 = parse(""" {"make":"ly", "price":350} """).extract[Bike]
     assert(b2 == Bike("ly",350))
     
     //println((json \ "name").extract[String])
     
     val json3 = parse("""
         {
           "name": "joe",
           "addresses": {
             "address1": {
               "street": "Bulevard",
               "city": "Helsinki"
             },
             "address2": {
               "street": "Soho",
               "city": "London"
             }
           }
         }""")
    val pa = json3.extract[PersonWithAddresses]
    //rintln(pa)
  }
  
  test("NoTypeHints Serialization test") {
    import example._
  
    implicit val formats = Serialization.formats(NoTypeHints)
    val ser = write(Child("Mary", 5, None))
    assert(ser == """{"$outer":{},"name":"Mary","age":5}""")
    val dser = read[Child](ser)
    //assert(dser == Child("Mary",5,None))
  }
  
  test("ShortTypeHints Serialization test") {
    trait Animal
    case class Dog(name: String) extends Animal
    case class Fish(weight: Double) extends Animal
    case class Animals(animals: List[Animal])

    implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Dog], classOf[Fish])))
    val ser = write(Animals(Dog("pluto") :: Fish(1.2) :: Nil))
    //assert(ser == """"{"$outer":{["$outer":{}},"animals":[{"jsonClass":"LiftJsonTest$$anonfun$8$Dog$3","$outer":{"$outer":{}},"name":"pluto"},{"jsonClass":"LiftJsonTest$$anonfun$8$Fish$3","$outer":{"$outer":{}]},"weight":1.2}]}""")

    val dser = read[Animals](ser)
    assert(dser == Animals(List(Dog("pluto"), Fish(1.2))))
  }
  
  test("XML test") {
    val xml =
      <users>
        <user>
          <id>1</id>
          <name>Harry</name>
        </user>
        <user>
          <id>2</id>
          <name>David</name>
        </user>
      </users>
    val json = toJson(xml)
    //println(pretty(render(json)))
    
    val transJson = json transform {
      case JField("id", JString(s)) => JField("id", JInt(s.toInt))
      case JField("user", x: JObject) => JField("user", JArray(x :: Nil))
    }
    //println(transJson)
    
    val vsXml = toXml(json)
  }
  
  test("simple complete test") {
    
  } 
}
