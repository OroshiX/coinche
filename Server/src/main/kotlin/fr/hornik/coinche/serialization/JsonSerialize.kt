package fr.hornik.coinche.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.text.SimpleDateFormat

object JsonSerialize {
    val mapper: ObjectMapper = ObjectMapper().setDateFormat(
            SimpleDateFormat("yyyyMMdd'T'HH:mm:ss"))

    fun <T> toJson(item: T): String =
            this.mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(item)

    inline fun <reified T> fromJson(x: String): T = this.mapper.readValue(x)
}