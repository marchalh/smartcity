package models

import play.api.libs.json.{Writes, Json}

/**
 * Classe permettant de créer un tuple de string
 */
class Tuple(text1:String, text2:String){
    val param1 = text1
    val param2 = text2
}

object Tuple{
    implicit val writer = new Writes[Tuple] {
        def writes(tuple:Tuple) = Json.obj(
            "param1" -> tuple.param1,
            "param2" -> tuple.param2
        )
    }
}