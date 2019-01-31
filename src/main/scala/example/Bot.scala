package example

import bot.connections.{Acquaintances, Attribute, Person}
import bot.handler.MessageHandler
import bot.memory.storage.Printer.TriePrinter
import example.brain.Manager
import example.brain.modules.{AgeAttr, JobAttr, NameAttr, PassionAttr}

import scala.annotation.tailrec
import scala.util.{Failure, Success}


class Bot extends Manager with MessageHandler {
  type Matcher = (Option[Map[Attribute, String]], List[String], List[String])

  implicit val acquaintances = Acquaintances.xmlStorage("xml.out")

  def startDemo(implicit acquaintances: Acquaintances): Unit = {
    def go(botLog: List[String] = List.empty,
           humanLog: List[String] = List.empty,
           people: List[Map[Attribute, String]]): Unit = {
      val message = scala.io.StdIn.readLine()
      if (message == "QUIT") {
        acquaintances.persist(acquaintances.add(people, currentSessionInformation.toMap) map (new Person(_)))
      }
      else {
        val updatedHumanLog = humanLog :+ message

        if (message == "Do you remember me?") {
          val possibleMatches = acquaintances.tryMatch(people, currentSessionInformation.toList, 10)

          val isMatch = matcher(possibleMatches, humanLog, botLog)
          isMatch match {
            case (None, bL, hL) => go(bL, hL, people)
            case (Some(p), bL, hL) =>
              currentSessionInformation = currentSessionInformation.empty ++ p
              go(bL, hL, acquaintances.forget(people, p))
          }
        }
        else {
          val updatedBotLog = botLog :+ handle(masterBrain, message, updatedHumanLog, botLog)
          println(updatedBotLog.last)

          go(updatedBotLog, humanLog, people)
        }
      }
    }

    val peopleXML = acquaintances.remember()

    val people = peopleXML match {
      case Success(p) => p.view.map(translate).map(_.flatten.toMap).toList
      case Failure(_) => println("There seem to be a problem loading up my memory..."); List.empty
    }

    masterBrain.print()
    go(people = people)
  }

  @tailrec
  final def matcher(people: List[Map[Attribute, String]],
                    humanLog: List[String],
                    botLog: List[String]): Matcher = {
    if (people.isEmpty) {
      val response = "Sorry, I do not seem to remember you."
      println(response)
      (None, humanLog, botLog :+ response)
    }
    else {
      val botMsg = "Does this represent you: " + people.head
        .filterNot(currentSessionInformation.toList.contains)
        .maxBy(_._1.weight)._2
      println(botMsg)

      val userMsg = scala.io.StdIn.readLine()

      if (userMsg == "Yes") {
        println("Ah, welcome back!")
        (Some(people.head), humanLog :+ userMsg, botLog :+ botMsg)
      }
      else
        matcher(people.tail, humanLog :+ userMsg, botLog :+ botMsg)
    }
  }

  /** The triple represents:
    * _1 : Attribute name
    * _2 : Attribute weight
    * _3 : Attribute value
    *
    * @param people - a list where every single element represent a person with all their traits
    * @return - every item from the list converted to a map of Attribute, String
    */
  def translate(people: List[(String, String, String)]): List[Map[Attribute, String]] = {
    val applier: PartialFunction[(String, String, String), Map[Attribute, String]] = {
      case ("AgeAttr", weight, ageValue) => Map(Attribute(AgeAttr, weight.toInt) -> ageValue)
      case ("NameAttr", weight, nameValue) => Map(Attribute(NameAttr, weight.toInt) -> nameValue)
      case ("PassionAttr", weight, passionValue) => Map(Attribute(PassionAttr, weight.toInt) -> passionValue)
      case ("Job", weight, jobValue) => Map(Attribute(JobAttr, weight.toInt) -> jobValue)
    }

    people collect applier
  }

  override def disapprovalMessages: Set[String] = Set("", "", "Changed the subject...")

  override def unknownHumanMessages: Set[String] = Set("Not familiar with this")
}
