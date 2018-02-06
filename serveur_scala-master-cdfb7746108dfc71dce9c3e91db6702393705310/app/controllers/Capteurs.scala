package controllers

import models.Capteur
import play.api.libs.json.Json
import play.api.mvc._
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import play.api.db._
import play.api.Logger
import javax.inject.Inject
import org.joda.time.DateTime
import models.Date


class Capteurs @Inject()(db: Database) extends Controller{
       
    def humiditeNow(id:String) = Action {
        Logger.info("Demande de l'humidité")
        getDB("sensorhum", id) match {
            case Some(x) => Ok(Json.toJson(new Capteur("humidite", x))).as("text/json; charset=utf-8")
            case None => NotFound("Aucune donnée")
        }     
    }

    def luminositeNow(id:String) = Action {
        Logger.info("Demande de la luminosité")
        getDB("sensorlum", id) match {
            case Some(x) => Ok(Json.toJson(new Capteur("luminosite", x))).as("text/json; charset=utf-8")
            case None => NotFound("Aucune donnée")
        }     
    }

    def temperatureNow(id:String) = Action {
        Logger.info("Demande de la température")
        getDB("sensortemp", id) match {
            case Some(x) => Ok(Json.toJson(new Capteur("temperature", x))).as("text/json; charset=utf-8")
            case None => NotFound("Aucune donnée")
        }
    }
    
    def setTemperature(id:String) = Action {request =>
        Logger.info("Mise à jour de la température")
        getValueFromRequest(request.body, "value") match {
            case Some(x) => {
                if (insertDB("sensortemp", id, x))
                    Ok("Ok")
                else
                    InternalServerError("Erreur de DB")
            }
            case None => BadRequest("Aucune donnée envoyés")
        }
     } 
        
    def setLuminosite(id:String) = Action {request =>
        Logger.info("Mise à jour de la luminosité")
        getValueFromRequest(request.body, "value") match {
            case Some(x) => {
                if (insertDB("sensorlum", id, x))
                    Ok("Ok")
                else
                    InternalServerError("Erreur de DB")
            }
            case None => BadRequest("Aucune donnée envoyés")
        }
    }
    
    def setHumidite(id:String) = Action {request =>
        Logger.info("Mise à jour de l'humidité")
        getValueFromRequest(request.body, "value") match {
            case Some(x) => {
                if (insertDB("sensorhum", id, x))
                    Ok("Ok")
                else
                    InternalServerError("Erreur de DB")
            }
            case None => BadRequest("Aucune donnée envoyés")
        }
    }
    
    /**
     * Récupère la valeur d'un capteur dans la DB
     */
    private def getDB(table:String, id:String) = {
        db.withConnection { conn =>
            try {
                val statement = conn.createStatement
                val resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE Id = " + id + " ORDER BY Ordre DESC")
                if (resultSet.next)
                    Some(resultSet.getDouble("Value"))
                else
                    None
            } 
            catch {
                case e:Exception => {
                    Logger.error("Erreur de récupération de valeur : " + e.getMessage)
                    None
                }
            }
            finally {
                 conn.close
            }
        }
    }
    
    /**
     * Insère un enregistrement dans la DB pour les capteurs
     */
    private def insertDB(table:String, id:String, value:String):Boolean = {
        db.withConnection { conn =>
            try {
                val statement = conn.createStatement
                val jour = Date.getDaysOfTime
                val date = new DateTime().toString
                statement.execute("INSERT INTO " + table + " (Id, Value, Date, Jour) VALUES ('" + id + "', '" + value + "', '" + date + "', '" + jour + "')")
                true
            } 
            catch {
                case e:Exception => {
                    Logger.error("Erreur d'enregistrement de valeur : " + e.getMessage)
                    false
                }
            }
            finally {
               conn.close
            }
        }
    }
    
    /**
     * Récupère la valeur du champ 'id' dans la requête 'content'
     */
    private def getValueFromRequest(content:AnyContent, id:String) = {
        Try(content.asFormUrlEncoded.get(id)(0)) match {
            case Success(x) => Some(x)
            case Failure(x) => None
        }
    }
}