package models

import play.api.libs.json.{Json, Writes}

/**
 * Classe représentant une plante, avec un nom, un intervalle de récolte ainsi qu'un intervalle de plantage
 */
class Plante(pNom:String, ppD:String, ppF:String, prD:String, prF:String){
    val nom = pNom
    val plantationDebut = ppD
    val plantationFin = ppF
    val recolteDebut = prD
    val recolteFin = prF
}

object Plante {
    implicit val writer = new Writes[Plante] {
        def writes(plante:Plante) = Json.obj(
            "nom" -> plante.nom,
            "plantationDebut" -> plante.plantationDebut,
            "plantationFin" -> plante.plantationFin,
            "recolteDebut" -> plante.recolteDebut,
            "recolteFin" -> plante.recolteFin
        )
    }
}
/**
 * Classe représentant un ensemble de plantes
 */
class Plantations(list:List[Plante]) {
    val plantes = list
}

object Plantations {
    implicit val writer = new Writes[Plantations] {
        def writes(plantation:Plantations) = Json.obj(
            "plantation" -> plantation.plantes
        )
    }
}