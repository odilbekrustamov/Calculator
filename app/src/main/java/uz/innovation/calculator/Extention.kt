package uz.innovation.calculator

import java.util.Locale

fun String.extractLastSegment(): String {
    var startIndex = this.length - 1
    while (startIndex >= 0 && this[startIndex] != '+' && this[startIndex] != '-') {
        startIndex--
    }
    return this.substring(startIndex + 1)
}

fun String.endsWithOperator(): Boolean {
    return !(endsWith('+') || endsWith('-') || endsWith('*') || endsWith('/'))
}

fun String.removeLeadingSpaces(): Float {
    return this.replace(" ", "").toFloat()
}

fun String.formatNumber(): String {
    this.replace(Regex("\\.0+$"), "").replace(Regex("\\.0+$"), "")

    try {
        val parts = String.format(Locale.US, "%.2f", this.replace(",", "").toBigDecimal()).split(".")
        val integralPart = parts[0].replace(",", "")
        val formattedIntegralPart = StringBuilder()

        var count = 0
        for (i in integralPart.length - 1 downTo 0) {
            formattedIntegralPart.insert(0, integralPart[i])
            count++
            if (count % 3 == 0 && i > 0) {
                formattedIntegralPart.insert(0, " ")
            }
        }

        return if (parts.size == 1) {
            formattedIntegralPart.toString()
        } else {
            if (parts[1].endsWith("00")){
                "${formattedIntegralPart}"
            }else  if (parts[1].endsWith("0")){
                "${formattedIntegralPart}.${parts[1].substring(0, parts[1].length-1)}"
            }else{
                "${formattedIntegralPart}.${parts[1]}"
            }
        }
    } catch (e: Exception) {
        return this
    }
}

fun String.isWholePartSizeGreaterThanNine(): Boolean {
    val numberWithoutSpaces = this.replace(" ", "")
    return try {
        val wholePart = numberWithoutSpaces.substringBefore(".")
        wholePart.length > 9
    } catch (e: NumberFormatException) {
        e.printStackTrace()
        false
    }
}
