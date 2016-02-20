
项目在Json解析时采用了lift-json.

### lift-json相关资源总结

+ [官方文档doc](http://liftweb.net/api/25/api/net/liftweb/json/package.html)
+ [github 项目地址](https://github.com/lift/framework/tree/master/core/json)

### 知识要点

#### Json AST (Abstract Syntax Tree 抽象语法树)

为每个Json的主要和架构上的类型提供了一个对应的case class。
```Scala
sealed abstract class JValue
case object JNothing extends JValue // 'zero' for JValue
case object JNull extends JValue
case class JString(s: String) extends JValue
case class JDouble(num: Double) extends JValue
case class JInt(num: BigInt) extends JValue
case class JBool(value: Boolean) extends JValue
case class JObject(obj: List[JField]) extends JValue    // 映射（键值对的链表）
case class JArray(arr: List[JValue]) extends JValue     // 链表或者数组
case class JField(String, JValue)                       // 键值对
```
用上面形式的AST实现了所有的特性。函数或者用于改变AST自己，或者转化为其他的形式。基本的转换表示如下图所示：
![lift-json](https://raw.githubusercontent.com/lift/framework/master/core/json/json.png)

#### 常用的函数定义

```Scala
def parse(s: String): JValue
def pretty(d: Document): String
def render(value: JValue): Document // 注意此处使用的scala.text.Document已经被废弃
def compact(d: Document): String
```

#### 特性总结

+ 解析Json很快
+ 有着和LINQ类似的查询接口
+ 可以用case class从已经解析过的Json中提取值
+ Diff & Merge
+ 用于生成JSON的DSL
+ XPath类似的表达式以及HOFs来操作JSON
+ 漂亮和压缩的打印(print)
+ 可以转化为XML
+ 序列化
+ 低层次的pull parser API

#### 安装

在sbt依赖中添加：
```
"net.liftweb" %% "lift-json" % "XXX"
```

### 使用的例子

[官方的例子(测试文档)](https://github.com/lift/framework/blob/master/core/json/src/test/scala/net/liftweb/json/Examples.scala)

#### 1 解析Json
```
import net.liftweb.json._
println(parse(""" { "numbers" : [1, 2, 3, 4] } """))
// JObject(List(JField(numbers,JArray(List(JInt(1), JInt(2), JInt(3), JInt(4))))))
```

#### 2 生成Json

##### 1) DSL规则

+ 原始类型映射为JSON原始类型
+ 所有Seq形式类型生成为JSON的数组类型

```
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

val jsons = List(1, 2, 3, 4)
// 注意在net.liftweb.json.Implicits包中定义了很多隐式的转换
// 可以将BigDecimal、BigInt、Boolean、double、float、int、long、string等转化为对应的JValue子类型。
// 但是还是没有看到将List转化为JValue的代码！？
println(compact(render(jsons)))
// [1,2,3,4]
```
+ Tuple2[String: A] 产生域(Field)

```
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

val jsont = ("name" -> "joe")
println(compact(render(jsont)))
// {"name":"joe"}
```

+ `~`操作产生组合域的对象

```
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

val jsonm = ("name" -> "joe") ~ ("age" -> 35)
println(compact(render(jsonm)))
// {"name":"joe","age":35}
```

+ 任何值都是可选的(Optional)。当其不包含值时，域和值都会被删除。

```
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

val jsonm2 = ("name" -> "joe") ~ ("age" -> Some(35))
println(compact(render(jsonm2)))
// {"name":"joe","age":35}
    
val jsonm3 = ("name" -> "joe") ~ ("age" -> (None: Option[Int]))
println(compact(render(jsonm3)))
// {"name":"joe"}
```

**最终的例子**

```
object JsonExample extends Application {
  import net.liftweb.json._
  import net.liftweb.json.JsonDSL._

  case class Winner(id: Long, numbers: List[Int])
  case class Lotto(id: Long, winningNumbers: List[Int], winners: List[Winner], drawDate: Option[java.util.Date])

  val winners = List(Winner(23, List(2, 45, 34, 23, 3, 5)), Winner(54, List(52, 3, 12, 11, 18, 22)))
  val lotto = Lotto(5, List(2, 45, 34, 23, 7, 5, 3), winners, None)

  val json = 
    ("lotto" ->
      ("lotto-id" -> lotto.id) ~
      ("winning-numbers" -> lotto.winningNumbers) ~
      ("draw-date" -> lotto.drawDate.map(_.toString)) ~
      ("winners" ->
        lotto.winners.map { w =>
          (("winner-id" -> w.id) ~
           ("numbers" -> w.numbers))}))

  println(compact(render(json)))
  
  pretty(render(JsonExample.json))
}
```

##### 2) Merging & Diffing

```Scala
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
    
println(pretty(render(mergedLotto)))
    
val Diff(changed, added, deleted) = mergedLotto diff lotto1
    
println(s"changed: $changed, added: $added, deleted: $deleted")
```

#### 3 查询Json


##### 1) "LINQ" style
 
```Scala
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
for { JField("age", JInt(age)) <- json } yield age
// List(5, 3)
for { JField("name", JString(name)) <- json} yield name
// List("joe", "Mary", "Mazy")
```
##### 2) XPath + HOFs

例子：
```Scala
{ 
  "person": {
    "name": "Joe",
    "age": 35,
    "spouse": {
      "person": {
        "name": "Marilyn"
        "age": 33
      }
    }
  }
}

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
// 此处直接生成的Tuple类型的值，但是应该也有一个隐式转换将其转化为JValue类型的值。

```
+ "\\\" : 查询域 (XPath-like expression to query JSON fields. Returns all matching fields.)

```
def \\(nameToFind: String): JValue
def \\[A <: JValue](clazz: Class[A]): List[Values]
```

```
json \\ "spouse"
// JObject(List(JField(person,JObject(List(JField(name,JString(Marilyn)), JField(age,JInt(33)))))))

json \\ "age"
// JObject(List(JField(age,JInt(35)), JField(age,JInt(33))))

json \\ "name"
```

+ "\\": XPath-like expression to query JSON fields. Matches only fields on next level.

```Scala
def \[A <: JValue](clazz: Class[A]): List[Values]
def \(nameToFind: String): JValue
```

```Scala
json \ "person" \ "name"
json \ "person" \ "spouse" \ "person" \ "name"
```

+ 其他相关的`JValue`方法

```
def filter(p: (JValue) ⇒ Boolean): List[JValue]
def find(p: (JValue) ⇒ Boolean): Option[JValue]
def fold[A](z: A)(f: (A, JValue) ⇒ A): A
def map(f: (JValue) ⇒ JValue): JValue
def remove(p: (JValue) ⇒ Boolean): JValue

abstract type Values // 各个子类定义不同
// JString: String
// JObjectr: type Values = Map[String, Any]
// JField: type Values = (String, Values)
// JArray: type Values = List[Any]
abstract def values: Values
```

#### 4 提取对象（Extracting values）
可以使用case class从以及解析过的Json中提取值。不存在的值会提取到Option对象上，而且字符串也可以自动地转化为java.util.Date。

例子：

```
import net.liftweb.json._
implicit val formats = DefaultFormats // Brings in default date formats etc.

case class Child(name: String, age: Int, birthdate: Option[java.util.Date])
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
```

默认地，构建器参数的名称必须和json中域的名字相一致。然而，有时候json中的域的名称可能不是有效的Scala标识符。对于这种情况，有两种解决方案。

+ Back ticks
```
case class Person(`first-name`: String)
```
+ 预处理
```
case class Person(firstname: String)
json transform {
  case JField("first-name", x) => JField("firstname", x)
}
```

+ 当case class中有多个构造器时，提取函数会尽可能地找到最匹配的那一个。

```
case class Bike(make: String, price: Int) {
  def this(price: Int) = this("Trek", price)
}
parse(""" {"price":350} """).extract[Bike]
```
+ 原始类型直接提取

```
(json \ "name").extract[String]
((json \ "children")(0) \ "birthdate").extract[Date]
```

+ 可以修改隐式的DataFormat对转化进行定义

```
implicit val formats = new DefaultFormats {
  override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
}
```

+ JSON object 也可以转化为`Map[String, _]`。

```
val json = parse("""
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
case class PersonWithAddresses(name: String, addresses: Map[String, Address])
json.extract[PersonWithAddresses]
```


#### 5 序列化（Serialization）

case class 可以被序列化也可以被反序列化。更多的测试可以参见
`src/test/scala/net/liftweb/json/SerializationExamples.scala`

API
```
// 1: net.liftweb.json.Serialization.
def read[A](in: Reader)(implicit formats: Formats, mf: Manifest[A]): A
def read[A](json: String)(implicit formats: Formats, mf: Manifest[A]): A

def write[A <: AnyRef, W <: Writer](a: A, out: W)(implicit formats: Formats): W
def write[A <: AnyRef](a: A)(implicit formats: Formats): String

// 2: net.liftweb.json.TypeHints
//    net.liftweb.json.FullTypeHints extends TypeHints
//    net.liftweb.json.NoTypeHints extends TypeHints
//    net.liftweb.json.ShortTypeHints extends TypeHints

```

```
import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}

implicit val formats = Serialization.formats(NoTypeHints)
val ser = write(Child("Mary", 5, None))
println(ser)
val dser = read[Child](ser)
println(dser)
```

**序列化支持**：

+ 任意深度的case class图
+ 所有的原始类型，包括BigInt和Symbol
+ List, Array, Set和Map (注意：Map的key必须是string类型: Map[String, _])
+ scala.Option
+ java.util.Date
+ Polymorphic Lists (see below)（多态列表）
+ Recursive types 递归类型
+ Custom serializer functions for types which are not supported (see below)

##### 1) Serializing polymorphic Lists

多态(polymorphic )/多相(heterogeneous)List在序列化时需要类型提示(type hints)。
此时序列化后的JSON对象将会得到一个额外的域，称为`jsonClass`(这个名称可以通过覆盖Formats中的`typeHintFieldName`来改变)。

```Scala
trait Animal
case class Dog(name: String) extends Animal
case class Fish(weight: Double) extends Animal
case class Animals(animals: List[Animal])

implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Dog], classOf[Fish])))
val ser = write(Animals(Dog("pluto") :: Fish(1.2) :: Nil))
println(ser)

val dser = read[Animals](ser)
println(dser)
```

ShortTypeHints会输出短类名，而FullTypeHints会输出完整类名。可以通过继承TypeHints特质实现其他的策略。

##### 2) Serializing non-supported types

可以通过相关的serializer和deserializer配置函数，使得任意类型均可序列化和反序列化。
当你拿到一个非case class的间距器，可以通过提供下面的序列化器实现序列化。

```
class Interval(start: Long, end: Long) {
  val startTime = start
  val endTime = end
}

class IntervalSerializer extends Serializer[Interval] {
  private val IntervalClass = classOf[Interval]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Interval] = {
    case (TypeInfo(IntervalClass, _), json) => json match {
      case JObject(JField("start", JInt(s)) :: JField("end", JInt(e)) :: Nil) =>
        new Interval(s.longValue, e.longValue)
      case x => throw new MappingException("Can't convert " + x + " to Interval")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Interval =>
       JObject(JField("start", JInt(BigInt(x.startTime))) :: 
          JField("end",   JInt(BigInt(x.endTime))) :: Nil)
  }
}

implicit val formats = Serialization.formats(NoTypeHints) + new IntervalSerializer
```

函数`serialize`创建了一个JSON对象来获得序列化后的数据。函数`deserialize`通过模式匹配的类型信息和数据
重构序列化的对象。

##### 3) Extensions
模块`lift-json-ext`包含了一些用于提取和序列化的扩展，支持以下类型：

```
// Lift's box
implicit val formats = net.liftweb.json.DefaultFormats + new JsonBoxSerializer

// Scala enums
implicit val formats = net.liftweb.json.DefaultFormats + new EnumSerializer(MyEnum)
// or
implicit val formats = net.liftweb.json.DefaultFormats + new EnumNameSerializer(MyEnum)

// Joda Time
implicit val formats = net.liftweb.json.DefaultFormats ++ JodaTimeSerializers.all
```

#### 6 XML支持
JSON结构可以转化为XML结点并反向转化。

API

```
def toJson(xml: NodeSeq): JValue
def toXml(json: JValue): NodeSeq // scala.xml.NodeSeq
```

例子

```
import net.liftweb.json.Xml.{toJson, toXml}

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
```

上面的转化存在连个问题：
1. id被转化为字符串
2. 转化函数使用了JSON Array

#### 7 Low level pull parser API
Pull parser API用于那些对性能有极端要求的情况。他提供了两种方式来提升性能：
1. AST不会立即产生。
2. 使用者可以在任意时刻停止解析，跳过剩余的部分。

### 项目中的使用

