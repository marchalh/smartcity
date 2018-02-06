package controllers

import models.Meteo
import models.Date
import play.api.libs.json.Json
import play.api.mvc._
import play.api.Logger
import scala.xml.XML
import models.Tuple
import javax.inject.Inject
import play.api.db._


class MeteoController @Inject()(db: Database) extends Controller{

    /**
     * Namur : 2790469
     * Bruxelles : 2800867
     * Liège : 2792411
     * Hainault : 2796741
     * Luxembourg : 2791993
     * Brabant Wallon : 3333251
     * Flandre-occidentale : 2783770
     * Flandre-orientale : 2789733
     * Anvers : 2803136
     * Limbourg : 2792347
     * Brabant Flamand : 3333250
     */
    val citiesID = Array("2790469", "2800867", "2792411", "2796741", "2791993", "3333251", "2783770", "2789733", "2803136", "2792347", "3333250")
  
    /**
     * Inscrit la météo pour demain dans la DB
     */
    def setMeteoDemain(id:String, temperature:Double, icon:String) = {
        db.withConnection { conn => {
            try {
                val statement = conn.createStatement()
                val queryRemove = "DELETE FROM meteodemain WHERE Id = " + id
                statement.executeUpdate(queryRemove)
                val queryInsert = "INSERT INTO meteodemain (Id, Temperature, Icon, Hier) VALUES (" + id + ", " + temperature + ", '" + icon + "'," + Date.getDaysOfTime + ")"
                Logger.info("Requete : " + queryInsert)
                statement.execute(queryInsert)
            } 
            catch {
                case e:Exception => Logger.error(e.getMessage)
            }
            finally {
                conn.close()
            }
          }
        }
    }
    
    /**
     * Renvoie la météo de demain
     */
    def meteoDemain(id:String) = Action {
        Logger.info("Demande de la météo de demain")
        var temperature = -9999.0
        var icon = ""
        db.withConnection { conn => {
            try {
                val statement = conn.createStatement()
                val query = "SELECT * FROM meteodemain, user WHERE user.Id = " + id + " AND user.Location = meteodemain.Id"
                val resultSet = statement.executeQuery(query)
                var ok = false
                if (resultSet.next()){
                    temperature = resultSet.getDouble("Temperature")
                    icon = resultSet.getString("Icon")
                    val hier = resultSet.getInt("Hier")
                    if (hier == Date.getDaysOfTime){
                        ok = true
                    }
                }
                if (!ok) {
                    getAllMeteoDemain
                    val statement = conn.createStatement()
                    val query = "SELECT * FROM meteodemain, user WHERE user.Id = " + id + " AND user.Location = meteodemain.Id"
                    val resultSet = statement.executeQuery(query)
                    if (resultSet.next()){
                        temperature = resultSet.getDouble("Temperature")
                        icon = resultSet.getString("Icon")
                    }
                }
            } 
            catch {
                case e:Exception => InternalServerError("Erreur interne")
            }
            finally {
                conn.close()
            }
          }
        }
        if (temperature != -9999.0)
            Ok(Json.toJson(new Meteo(temperature, "Any", icon))).as("text/json; charset=utf-8")
        else
            NotFound("Aucune donnée")
    }
  
    /**
     * Recupère la météo de demain pour toutes les villes
     */
    def getAllMeteoDemain = {
        for {
            i<-citiesID
        } getMeteoByCity(i)
    }
  
    /**
     * Récupère la météo pour la ville 'id'
     */
    def getMeteoByCity(id:String) = {
        var url = "http://api.openweathermap.org/data/2.5/forecast/daily"
        url = url + "?id=" + id
        url = url + "&cnt=1"
        url = url + "&APPID=46847b326020e8a1020daa6e576ce1e8&mode=xml&units=metric"
        val meteo = scala.xml.XML.load(url)
        val symbolIcon = meteo \\ "symbol" \ "@var"
        val temp = meteo \\ "temperature" \ "@day"
        setMeteoDemain(id, temp.text.toDouble, symbolIcon.toString)
    }  
 }

