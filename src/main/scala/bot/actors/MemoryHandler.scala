package bot.actors

import akka.actor.{Actor, Props}
import bot.memory.Person
import bot.trie.Attribute

import scala.util.Try

object MemoryHandler {
  def props() = Props(new MemoryHandler)
  def name() = "memoryHandler"

  case class Save(file: String, people: List[Person])


  case class Load(file: String)


  case class Forget(people: List[Map[Attribute, String]],
                    person: Map[Attribute, String])

  case class Add(people: List[Map[Attribute, String]],
                 person: Map[Attribute, String])

  case class TryAndMatch(people: List[Map[Attribute, String]],
                         person: List[(Attribute, String)],
                         minThreshold: Int)

  case class LoadedPeople(people: Try[List[List[(String, String, String)]]])

  case class PossibleMatches(people: List[Map[Attribute, String]])

  case class RetrievePeople(people: List[Map[Attribute, String]])
}

class MemoryHandler() extends Actor{
  override def receive: Actor.Receive = {
    case _ => "asdfas"
  }
}