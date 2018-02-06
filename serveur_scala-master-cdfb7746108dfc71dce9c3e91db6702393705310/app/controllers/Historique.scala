package controllers

import models.Capteur
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Logger
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import play.api.db._
import javax.inject.Inject
import org.joda.time.DateTime
import org.joda.time.Days
import models.Histo
import models.Date
import org.joda.time.Minutes

class Historique @Inject()(db: Database) extends Controller{
  
    /**
     * Renvoie l'historique de luminosité
     */
    def historiqueLuminosite(id:String) = Action {
        Logger.info("Demande de l'historique de luminosité pour l'ID : " + id)
        val jour = Date.getDaysOfTime
        val values = (for {
            i<-0 until 7
        } yield getLuminositeByDay(id, jour-i)).toArray
        val histo = new Histo(values)
        Ok(Json.toJson(histo)).as("text/json; charset=utf-8")
    }
    
    def historiqueHumidite(id:String) = Action {
        Logger.info("Demande de l'historique d'humidité pour l'ID : " + id)
        val jour = Date.getDaysOfTime
        val values = (for {
            i<-0 until 7
        } yield getHumiditeByDay(id, jour-i)).toArray
        val histo = new Histo(values)
        Ok(Json.toJson(histo)).as("text/json; charset=utf-8")
    }
    
    def historiqueTemperature(id:String) = Action {
        Logger.info("Demande de l'historique de température pour l'ID : " + id)
        val jour = Date.getDaysOfTime
        val values = (for {
          i<-0 until 7
        } yield getTemperatureByDay(id, jour-i)).toArray
        val histo = new Histo(values)
        Ok(Json.toJson(histo)).as("text/json; charset=utf-8")
    } 
    
    /**
     * Returne la température à midi pour le jour 'day' pour l'id 'id'
     */
    private def getTemperatureByDay(id:String, day:Int):Double = {
        var result = 0.0
        db.withConnection { conn =>
            try {
                val statement = conn.createStatement
                val resultSet = statement.executeQuery("SELECT * FROM sensortemp WHERE Id = " + id + " AND Jour = " + day + " ORDER BY Ordre ASC")
                var end = false
                while (resultSet.next && !end){
                    val date = DateTime.parse(resultSet.getString("Date"))
                    if (date.getHourOfDay >= 12)
                    {
                        result = resultSet.getDouble("Value")
                        end = true
                    }
                }
                if (result == 0)
                    result = -9999
            } 
            catch {
                case e:Exception => result = -9999
            }
            finally {
                conn.close
            }
        }
        result
    }
    
    /**
     * Retourne la luminosité du jour 'day' de l'id 'id' en heure
     */
    private def getLuminositeByDay(id:String, day:Int):Double = {
        var result = 0.0
        db.withConnection { conn =>
            try {
                val statement = conn.createStatement
                val resultSet = statement.executeQuery("SELECT * FROM sensorlum WHERE Id = " + id + " AND Jour = " + day + " ORDER BY Ordre ASC")
                var soleil = false
                var lastDate:DateTime = null
                while (resultSet.next){
                    val value = resultSet.getDouble("Value")
                    if (value >= 400)
                    {
                        if (soleil)
                        {
                            val nowDate = DateTime.parse(resultSet.getString("Date"))
                            result += Minutes.minutesBetween(lastDate, nowDate).getMinutes
                        }
                        soleil = true
                        lastDate = DateTime.parse(resultSet.getString("Date"))
                    }
                    else
                    {
                        soleil = false
                    }
                }
                if (result == 0)
                    result = -9999
                else
                    result = result/60.0
            } 
            catch {
                case e:Exception => result = -9999
            }
            finally {
                conn.close
            }
        }
        result
    }
    
    /**
     * Retourne la moyenne de l'humidité du jour 'day' de l'id 'id'
     */
    private def getHumiditeByDay(id:String, day:Int):Double = {
        var result = 0.0
        db.withConnection { conn =>
            try {
                val statement = conn.createStatement
                val resultSet = statement.executeQuery("SELECT * FROM sensorhum WHERE Id = " + id + " AND Jour = " + day + " ORDER BY Ordre DESC")
                var count = 0.0
                while (resultSet.next){
                    result += resultSet.getDouble("Value")
                    count += 1.0
                }
                if (count == 0.0)
                    result = -9999
                else
                    result = result/count
            } 
            catch {
                case e:Exception => result = -9999
            }
            finally {
                conn.close
            }
        }
        result
    }    
}