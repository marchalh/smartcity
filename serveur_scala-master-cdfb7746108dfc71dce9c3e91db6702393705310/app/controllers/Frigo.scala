package controllers

import javax.inject.Inject
import play.api.db._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Logger
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import models.Date
import scala.collection.mutable.ArrayBuffer
import models.Aliments
import models.Aliment

class Frigo @Inject()(@NamedDatabase("frigo") db: Database) extends Controller{
  
    /**
     * Scan d'une puce RFID : Si elle existe on la supprime, sinon on la crée
     */
    def scan = Action { request =>
        Logger.info("Scan d'une puce RFID")
        getValueFromRequest(request.body, "rfid") match {
            case Some(x) => {
                val rfid = x
                Logger.info("Tag : " + rfid)
                db.withConnection { conn =>
                    try {
                        val statement = conn.createStatement
                        val rs = statement.executeQuery("SELECT * FROM aliment WHERE RFID='" + rfid + "'")
                        if (rs.next){
                            statement.execute("DELETE FROM aliment WHERE RFID='" + rfid + "'")
                        }
                        else {
                            val jour = Date.getDaysOfTime + RFIDToDays(rfid)
                            statement.execute("INSERT INTO aliment (RFID, DateEnd) VALUES ('" + rfid + "', " + jour + ")")
                        }
                    } 
                    catch {
                        case e:Exception => InternalServerError("Erreur interne")
                    }
                    finally {
                        conn.close
                    }
                }
                Ok("RFID : " + rfid)
            }
            case None => BadRequest("Pas de RFID")
        }
    }
  
    /**
     * Renvoie la liste des aliments dans le frigo
     */
    def aliments = Action {
        Logger.info("Demande de la liste des aliments")
        val list = ArrayBuffer[Aliment]()
        db.withConnection { conn =>
            try {
          	    val stmt = conn.createStatement
          	    val rs = stmt.executeQuery("SELECT * FROM aliment")
                while (rs.next()) {
	            	    val aliment = new Aliment(rs.getString("Label"), rs.getInt("DateEnd") - Date.getDaysOfTime, RFIDToNum(rs.getString("RFID")))
	            	    list += aliment
	              }
            } 
            catch {
        	      case e:Exception => InternalServerError("Erreur interne")
            } 
            finally {
                conn.close()
            }
        }
        val aliments = new Aliments(list.toList)
        Ok(Json.toJson(aliments))
    }
  
    /**
     * Ajoute un label à un aliment
     */
    def setLabel = Action {request =>
        Logger.info("Demande d'ajout d'un label")
        try {
            val label = request.body.asFormUrlEncoded.get("label").mkString
            val numero = Integer.valueOf(request.body.asFormUrlEncoded.get("numero").mkString)
            db.withConnection { conn => {
                try {
                    val statement = conn.createStatement()
                    val query = "UPDATE aliment SET Label='" + label + "' WHERE RFID='" + NumToRFID(numero) + "'"
                    statement.executeUpdate(query)
                    Ok("OK")
                } 
                catch {
                    case e:Exception => InternalServerError("Erreur interne")
                }
                finally {
                    conn.close()
                }
            }
          }
        } 
        catch {
            case e:Exception => BadRequest("Manque de paramètre")
        }
    }
   
    /**
     * Transforme un tag RDID en nombre de jours avant péremption
     */
    private def RFIDToDays(RFID:String):Int = RFIDToNum(RFID) match { 
        case 1 => 4
        case 2 => 4
        case 3 => 2
        case 4 => 2
        case 5 => 2
        case 6 => 7
        case 7 => 7
        case _ => 0
    }
  
    /**
     * Transforme un tag RFID en numéro inscrit sur le tag
     */
    private def RFIDToNum(RFID:String):Int = RFID match {
        case "4a0037d2c3" => 1
        case "4a003732e7" => 2
        case "number3" => 3
        case "number4" => 4
        case "number5" => 5
        case "number6" => 6
        case "number7" => 7
        case _ => 0
    }
  
    /**
     * Transforme le numéro écrit sur le tag en tag RFID
     */
    private def NumToRFID(Num:Int):String = Num match {
        case 1 => "4a0037d2c3"
        case 2 => "4a003732e7"
        case 3 => "number3"
        case 4 => "number4"
        case 5 => "number5"
        case 6 => "number6"
        case 7 => "number7"
        case _ => "bl4-5s"
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