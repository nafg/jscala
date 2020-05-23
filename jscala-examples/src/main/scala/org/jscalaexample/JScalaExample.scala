package org.jscalaexample

import org.jscala._
import scala.util.Random
import org.scalajs.dom._

case class User(name: String, id: Int)

object JScalaExample {
  def domManipulations(): Unit = {
    val html = javascript {
      val node = document.getElementById("myList2").lastChild
      document.getElementById("myList1").appendChild(node)
      val buttons = document.getElementsByTagName("link")
      for (idx <- 0 until buttons.length) {
        console.log(buttons.item(idx).attributes)
      }
    }.asString
    println(html)
  }

  def hello(): Unit = {
    val ast = javascript {
      def main(args: Array[String]): Unit = {
        val language = if (args.length == 0) "EN" else args(0)
        val res = language match {
          case "EN" => "Hello!"
          case "FR" => "Salut!"
          case "IT" => "Ciao!"
          case _ => s"Sorry, I can't greet you in $language yet"
        }
        print(res)
      }
    }
    println(ast.asString)
  }

  def browserStuff(): Unit = {
    val js = javascript {
      window.location.href = "http://jscala.org"
      window.open("https://github.com")
      window.history.back()
    }
    println(js.asString)
  }

  def shortExample(): Unit = {
    val scalaValue = "https://github.com/nau/jscala"
    def rand() = Random.nextInt(5).toJs
    val $ = new JsDynamic {}

    val js = javascript {
      window.setTimeout(() => {
        val links = Array("https://github.com/nau/scala")
        include("var raw = 'JavaScript'")
        for (link <- links) {
          $("#id").append(s"<p>$link</p>")
        }
        for (i <- 0 to rand().as[Int]) print(inject(scalaValue))
      }, 1000)
    }
    println(js.asString)
  }

  def complexExample(): Unit = {
    val js = javascript {
      window.setTimeout(() => {
        val r = new RegExp("d.*", "g")
        class Point(val x: Int, val y: Int)
        val point = new Point(1, 2)
        def func(i: String) = r.exec(i)
        val list = document.getElementById("myList2")
        val map = collection.mutable.Map[String, String]()
        if (typeof(map) == "string") {
          for (idx <- 0 until list.attributes.length) {
            val attr = list.attributes.item(idx)
            map(attr.name) = func(attr.textContent)
          }
        } else {
          val obj = new {
            val field = 1
            def func2(i: Int) = "string"
          }
          val links = Array("https://github.com/nau/scala")
          for (link <- links) {
            include("var raw = 'JavaScript'")
            console.log(link + obj.func2(obj.field) + point.x)
          }
          window.location.href = links(0).replace("scala", "jscala")
        }
      }, 1000)
    }
    println(js.asString)
  }

  def ajaxExample(): Unit = {
    val $ = new JsDynamic {}
    def ajaxCall(pageId: Int) = javascript {
      $.get("ajax/" + pageId, (data: String) => $("#someId").html(data))
    }
    def genAjaxCall(pageId: Int) = javascript {
      ajaxCall(pageId)
    }

    println(genAjaxCall(123).asString)
  }


  import play.api.libs.json._
  def readmeExample(): Unit = {
    implicit val userJson = Json.format[User]
    @Javascript class Greeter {
      def hello(u: User): Unit = {
        print(s"Hello, ${u.name} \n")
      }
    }
    // Run on JVM
    val u1 = User("Alex", 1)
    val greeter = new Greeter()
    greeter.hello(u1) // prints "Hello, Alex"
    val json = Json.stringify(Json.toJson(u1))
    val main = javascript {
        val u = User(id = 2, name = "nau")
        // read User from json string generated above
        val u1Json = eval(s"(${include(json)})").as[User]
        val t = new Greeter()
        t.hello(u)
        t.hello(u1Json)
      }
    // join classes definitions with main code
    val js = Greeter.jscala.javascript ++ main
    js.eval() // run using Rhino
    println(js.asString) // prints resulting JavaScript
  }


  def astManipulation(): Unit = {
    val vardef = JsVarDef("test", "Test".toJs).block
    val print = JsCall(JsIdent("print"), JsIdent("test") :: Nil)
    val ast = vardef ++ print
    ast.eval()
    println(ast.asString)
    /* prints
    Test
    {
     var test = "Test";
     print(test);
    }
     */
  }

  def main(args: Array[String]): Unit = {
    domManipulations()
    browserStuff()
    complexExample()
    hello()
    shortExample()
    readmeExample()
    astManipulation()
  }
}

