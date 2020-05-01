package fr.hornik.coinche.util

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
        dbgLevel.SCORE -> "Score Traces"
        dbgLevel.HTML -> "HTML Traces"
        dbgLevel.MISC -> "MISC Traces"
        dbgLevel.ALL -> "Generic Traces"
        else -> "Other Traces"
    }
    if ((wantedLevel and traceLevel) != 0) {
        println("$addtlTraces : $wantedLevel : $Str")
    }


}