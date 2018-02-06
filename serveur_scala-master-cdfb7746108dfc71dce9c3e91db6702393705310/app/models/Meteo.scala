package models

import play.api.libs.json.{Writes, Json}

/**
 * Classe représentant la météo, avec la température, la description ainsi que le chemin vers l'icone correspondante
 */
class Meteo(temp:Double, text:String, ico:String){
    val temperature = temp
    val texte = text
    val icone = ico
}

object Meteo{
    implicit val writer = new Writes[Meteo] {
        def writes(meteo:Meteo) = Json.obj(
            "temperature" -> meteo.temperature,
            "texte" -> meteo.texte,
            "icone" -> meteo.icone
        )
    }
}