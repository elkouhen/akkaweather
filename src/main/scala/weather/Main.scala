import akka.actor.{ActorSystem, ActorLogging, Actor, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

import scala.collection.immutable._

import org.json4s._
import org.json4s.jackson.JsonMethods._

import dispatch._

import java.net.ConnectException
import com.ning.http.client.Response


class LocationService extends Actor with ActorLogging {

  val headers = Map("X-Mashape-Key" -> "oz5OTrdrQnmshayy6rDZZ0D7YCBCp16qCMhjsn8QxeG5h7mHCB",
    "Accept" -> "application/json")

  val req = :/("devru-latitude-longitude-find-v1.p.mashape.com", 443).secure / "latlon.php" <<? Map("location" -> "Nantes") <:< headers
  
  def receive = {
    case _ =>
      
      val responseFuture = for (res <- Http(req OK as.String)) yield res

      //println (responseFuture.get)
      sender ! responseFuture
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

  
  val req = host("devru-latitude-longitude-find-v1.p.mashape.com", 443).secure / "latlon.php?location=Nantes" 
  

  val locationService = system.actorOf(Props(new LocationService), "locationService")
  val weatherService = system.actorOf(Props(new WeatherService), "weatherService")

  for (location <- ask(locationService, "Nantes").mapTo[String]) yield {
    for (weatherJSON <- ask(weatherService, location).mapTo[String]) yield {
      
      println (compact(render(parse("" + weatherJSON + "") \ "msg")))
    }
  }

  system.shutdown
}
