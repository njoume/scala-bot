package example

import bot.handler.MessageHandler
import bot.memory.{BotMemory, Person}
import bot.trie.Attribute
import example.brain.Manager
import example.brain.modules.AgeAttr


class Bot extends Manager with MessageHandler with BotMemory {
  def startDemo(): Unit = {
    disapprovalMessages = Set("", "", "Changed the subject...")

    /*persist(List(new Person(Map(Attribute(AgeAttr, 10) -> "123",
      Attribute(AgeAttr, 15) -> "12",
      Attribute(AgeAttr, 14) -> "12")),
      new Person(Map(Attribute(AgeAttr, 10) -> "123",
        Attribute(AgeAttr, 15) -> "12",
        Attribute(AgeAttr, 14) -> "12"))), "out.xml")*/

    remember("out.xml").foreach(println)

    /*println(tryMatch(
      List(Map(Attribute(AgeAttr, 10) -> "123",
        Attribute(AgeAttr, 15) -> "12",
        Attribute(AgeAttr, 14) -> "14"),
        Map(Attribute(AgeAttr, 10) -> "123",
          Attribute(AgeAttr, 15) -> "12",
          Attribute(AgeAttr, 14) -> "13")),
      Map(Attribute(AgeAttr, 15) -> "12",
        Attribute(AgeAttr, 14) -> "13").toList,
      15
    ))*/
    /*breakable {
      while (true) {
        val message = scala.io.StdIn.readLine()
        if (message == "QUIT") break()
        else println(handle(masterBrain, message))
      }
    }*/
  }
}
