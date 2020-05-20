package fr.hornik.coinche.util

import java.time.LocalDateTime

enum class dbgLevel(val value: Int) {
    NONE(0), DEBUG(1), FUNCTION(2), LOGIN(4), HTML(8), HTMLFUNC(10), MISC(16), REGULAR(32), ALL (63);

    infix fun and(traceLevel: dbgLevel): Any {
        return traceLevel.value and this.value

    }
    infix fun or(traceLevel: dbgLevel): Any {
        return traceLevel.value or this.value

    }
    fun toInt():Int {
        return this.value
    }
}

var traceLevel: dbgLevel = dbgLevel.REGULAR

fun debugPrintln(wantedLevel: dbgLevel, Str: Any) {

    val addtlTraces = when (wantedLevel) {
        dbgLevel.DEBUG -> "*****DEBUG******"
        dbgLevel.FUNCTION -> "Entering Function"
        dbgLevel.LOGIN -> "Score Traces"
        dbgLevel.HTML -> "HTML Traces"
        dbgLevel.MISC -> "MISC Traces"
        dbgLevel.REGULAR -> "Coinche"
        else -> "Other Traces"

        // Wantedlevel cannot be a combination of levels ( e.g. ALL pr HTMLFUNC)
    }
    val currentTime = LocalDateTime.now()
    if ((wantedLevel and traceLevel) != 0) {
        println("[$currentTime] $addtlTraces : $Str")
    }


}