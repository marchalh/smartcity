package models

import play.api.libs.json.{Json, Writes}

/**
 * Classe représentant un aliments, avec un label, un numéro de tag ainsi qu'un nombre de jours restants
 */
class Aliment(pLabel:String, pJours:Int, pNumero:Int) {
    val label = pLabel
    val jours = pJours
    val numero = pNumero
}

object Aliment {
    implicit val writer = new Writes[Aliment] {
        def writes(aliment:Aliment) = Json.obj(
            "label" -> aliment.label,
            "jours" -> aliment.jours,
            "numero" -> aliment.numero
        )
    }
}

/**
 * Classe représentant un ensemble d'aliments
 */
class Aliments(list:List[Aliment]) {
    val aliments = list
}

object Aliments {
    implicit val writer = new Writes[Aliments] {
        def writes(al:Aliments) = Json.obj(
            "aliments" -> al.aliments
        )
    }
}