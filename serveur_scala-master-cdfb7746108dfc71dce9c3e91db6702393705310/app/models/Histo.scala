package models

import play.api.libs.json.{Json, Writes}

/**
 * Classe représentant un historique de 7 jours
 */
class Histo(vals:Array[Double]) {
    val aujd = vals(0)
    val jour1 = vals(1)
    val jour2 = vals(2)
    val jour3 = vals(3)
    val jour4 = vals(4)
    val jour5 = vals(5)
    val jour6 = vals(6)
}

object Histo {
    implicit val writer = new Writes[Histo] {
        def writes(histo:Histo) = Json.obj(
            "aujd" -> histo.aujd,
            "jour1" -> histo.jour1,
            "jour2" -> histo.jour2,
            "jour3" -> histo.jour3,
            "jour4" -> histo.jour4,
            "jour5" -> histo.jour5,
            "jour6" -> histo.jour6
        )
    }
}