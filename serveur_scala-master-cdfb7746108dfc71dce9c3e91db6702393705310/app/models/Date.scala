package models

import org.joda.time.DateTime
import org.joda.time.Days

object Date {
    /**
     * Permet d'obtenir le nombre de jours depuis le 01/01/1970
     */
    def getDaysOfTime() = {
        Days.daysBetween(new DateTime(1970, 1, 1, 0, 0), new DateTime).getDays
    }
}