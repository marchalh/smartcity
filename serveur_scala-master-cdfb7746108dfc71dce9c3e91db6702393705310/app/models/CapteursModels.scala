package models

import play.api.libs.json.{Json, Writes}

/**
 * Classe représentant un capteur, avec un type (temperature, humidite,...) et une valeur
 */
class Capteur(t:String, v:Double){
    val typeC = t
    val value = v
}

object Capteur {
    implicit val writer = new Writes[Capteur] {
        def writes(capteur:Capteur) = Json.obj(
            "valeur" -> capteur.value,
            "type" -> capteur.typeC
        )
    }
}