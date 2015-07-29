import akka.actor.{ActorSystem, ActorLogging, Actor, Props}

import akka.pattern.{ask, pipe}
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

import org.json4s._
import org.json4s.jackson.JsonMethods._

class LocationService extends Actor with ActorLogging {
  def receive = {
    case _ => sender ! "{ \"lat\" : \"1\" : \"lon\" : \"1\" }"
  }
}

class WeatherService extends Actor with ActorLogging {
  def receive = {
    case _ => sender ! "{ \"msg\" : \"il fait beau\"}"
  }
}

object MainAkka extends App {
  val system = ActorSystem("akkaweather")

  implicit val timeout = Timeout(5 seconds)

  val locationService = system.actorOf(Props(new LocationService), "locationService")
  val weatherService = system.actorOf(Props(new WeatherService), "weatherService")

  for (location <- ask(locationService, "Nantes").mapTo[String]) yield {
    for (weatherJSON <- ask(weatherService, location).mapTo[String]) yield {
      
      println (compact(render(parse("" + weatherJSON + "") \ "msg")))
    }
  }

  system.shutdown
}
