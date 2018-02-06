package controllers

import models.Meteo
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Logger
import scala.xml.XML
import models.Tuple
import javax.inject.Inject
import play.api.db._
import models.Plante
import models.Plantations
import scala.collection.mutable.ListBuffer
import java.util.UUID


class Potager @Inject()(db: Database) extends Controller{

    /**
     * Vérifie l'id et le mdp d'un utilisateur  
     */
    def login() = Action { request =>
        Logger.info("Demande de Login")
        var error = false
        try {
            val id = request.body.asFormUrlEncoded.get("id").mkString
            val mdp = request.body.asFormUrlEncoded.get("password").mkString
            db.withConnection { conn => {
                try {
                    val statement = conn.createStatement()
                    val query = "SELECT * FROM user WHERE Id = '" + id + "' AND Mdp = '" + mdp + "'"
                    Logger.info("Query : " + query)
                    val resultSet = statement.executeQuery(query)
                    if (resultSet.next()){
                        Ok("OK")
                    }
                    else{
                        NotFound("Mauvaise combinaison")
                    }
                } catch {
                    case e:Exception => InternalServerError("Erreur interne")
                }
                finally {
                    conn.close()
                }
              }
            }
        } catch {
            case e:Exception => BadRequest("Erreur de paramètres")
        }
    }
  
    /**
     * Fournit un id libre et génère un mdp 
     */
    def newID() = Action { request =>
        Logger.info("Demande d'id et mdp")
        var name = "Any"
        var location = "Any"
        var error = false
        request.getQueryString("name") match {
            case Some(x) => name = x
            case None => error = true
        }
        request.getQueryString("location") match {
            case Some(x) => location = x
            case None => error = true
        }
        Logger.info("Requête OK ? " + !error)
        if (!error)
        {
            var id = "0"
            var mdp = "mdp"
            db.withConnection { conn => {
                try {
                    val statement = conn.createStatement()
                    val query = "SELECT Id FROM user ORDER BY CAST(Id AS UNSIGNED) DESC"
                    val resultSet = statement.executeQuery(query)
                    if (resultSet.next()){
                        id = String.valueOf(Integer.valueOf(resultSet.getString("Id")) + 1)
                        mdp = UUID.randomUUID.toString.replaceAll("-", "").substring(0, 8)
                        val statement = conn.createStatement()
                        val query = "INSERT INTO user (Id, Name, Location, Mdp) VALUES ('" + id + "', '" + name + "', '" + location + "', '" + mdp + "')"
                        Logger.info("Query : " + query)
                        statement.execute(query)
                    }
                } catch {
                    case e:Exception => InternalServerError("Erreur interne")
                } finally {
                    conn.close()
                }
              } 
            }
            Ok(Json.toJson(new Tuple(id, mdp)))
        }
        else {
            BadRequest("Erreur de paramètres")
        }
    }
    
    /**
     * Fournit la liste des plantations disponibles, ainsi que leurs dates de récolte et plantations
     */
    def plantations = Action {
        Logger.info("Demande des plantations")
        val list = ListBuffer[Plante]()
        db.withConnection { conn =>
            try {
          	    val stmt = conn.createStatement
          	    val rs = stmt.executeQuery("SELECT * FROM plante")
                while (rs.next()) {
	            	    list += new Plante(rs.getString("NomPlante"), rs.getString("DatePlantationDebut"),rs.getString("DatePlantationFin"),rs.getString("DateRecolteDebut"),rs.getString("DateRecolteFin"))
	              }
            } catch {
        	      case e:Exception => InternalServerError("Erreur interne")
            } finally {
                conn.close()
            }
        }
        Ok(Json.toJson(new Plantations(list.toList)))
    }
}

