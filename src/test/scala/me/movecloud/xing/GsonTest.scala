import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import com.google.gson._

@RunWith(classOf[JUnitRunner])
class GsonTest extends FunSuite {
  /**
  test("xxx") {        
    assert(true)
    intercept[Exception] { 
      //code 
    }
  }
  */
  
  test("simple toJson") {
    val gson: Gson = new Gson()
    assert(gson.toJson(1) == "1")
    assert(gson.toJson("abcd") == "\"abcd\"")
    assert(gson.toJson(10.toLong) == "10")
    //int[] values = { 1 };
    //gson.toJson(values);
  }
  
}