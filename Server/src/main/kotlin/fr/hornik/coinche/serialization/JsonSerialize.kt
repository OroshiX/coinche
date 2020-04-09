package fr.hornik.coinche.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass

object JsonSerialize {
    val mapper = ObjectMapper().setDateFormat(
            SimpleDateFormat("yyyyMMdd'T'HH:mm:ss"))

    fun <T> toJson(item: T): String =
            this.mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(item)

    inline fun <reified T> fromJson(x: String): T = this.mapper.readValue(x)
//    fun <T> fromJsonWithClass(x: String, classObj: Class<T>): T =
//            this.serializer.fromJson(x, classObj)

}

class SealedClassTypeAdapter<T : Any>(private val klass: KClass<Any>,
                                      private val gson: Gson) :
        TypeAdapter<T>() {
    override fun read(jsonReader: JsonReader): T {
        jsonReader.beginObject() // start reading the object
        val nextName = jsonReader.nextName() // get the name on the object
        val innerClass = klass.sealedSubclasses.firstOrNull {
            it.simpleName!!.contains(nextName)
        } ?: throw Exception(
                "$nextName is not found to be a data class of the sealed class ${klass.qualifiedName}")
        val x = gson.fromJson<T>(jsonReader, innerClass.javaObjectType)
        jsonReader.endObject()
        // if there a static object, actually return that back to ensure equality and such!
        return innerClass.objectInstance as T? ?: x
    }

    override fun write(out: JsonWriter, value: T) {
        val jsonString = gson.toJson(value)
        out.beginObject()
        out.name(value.javaClass.canonicalName.splitToSequence(".").last())
                .jsonValue(jsonString)
        out.endObject()
    }

}