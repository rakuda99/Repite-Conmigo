import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileReader

fun main() {
    val reader = FileReader("d:\\MY APP\\Repite Conmigo\\app\\src\\main\\assets\\lessons.json")
    val type = object : TypeToken<Map<String, List<Map<String, Any>>>>() {}.type
    val data: Map<String, List<Map<String, Any>>> = Gson().fromJson(reader, type)
    reader.close()

    val currentLang = "ar"
    data.forEach { (level, lessons) ->
        lessons.forEach { lessonMap ->
            val title = lessonMap["title"].toString()
            if (title.contains("School")) {
                val rawSentences = lessonMap["sentences"] as? List<Map<String, String>> ?: emptyList()
                rawSentences.forEach { sMap ->
                    val es = sMap["es"]
                    val ar = sMap["ar"]
                    val en = sMap["en"]
                    val translation = if (currentLang == "en") {
                        sMap["en"] ?: sMap["es"] ?: ""
                    } else {
                        sMap["ar"] ?: sMap["en"] ?: ""
                    }
                    if (es?.contains("Voy a la escuela") == true) {
                        println("ES: $es")
                        println("AR: $ar")
                        println("EN: $en")
                        println("COMPUTED TRANSLATION: $translation")
                    }
                }
            }
        }
    }
}
