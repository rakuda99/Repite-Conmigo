package com.repite.conmigo.logic

import com.repite.conmigo.data.Sentence

object NumberLessonGenerator {

    fun getSpanishNumber(n: Int): String {
        if (n == 100) return "cien"
        val units = listOf("", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve")
        val tens = listOf("", "diez", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa")
        
        if (n < 10) return units[n]
        if (n in 11..15) {
            return when (n) {
                11 -> "once"
                12 -> "doce"
                13 -> "trece"
                14 -> "catorce"
                15 -> "quince"
                else -> ""
            }
        }
        if (n in 16..19) {
            return when (n) {
                16 -> "dieciséis"
                else -> "dieci" + units[n % 10]
            }
        }
        if (n == 20) return "veinte"
        if (n in 21..29) {
            return when (n) {
                21 -> "veintiuno"
                22 -> "veintidós"
                23 -> "veintitrés"
                26 -> "veintiséis"
                else -> "veinti" + units[n % 10]
            }
        }
        val t = n / 10
        val u = n % 10
        return if (u == 0) {
            tens[t]
        } else {
            "${tens[t]} y ${units[u]}"
        }
    }

    fun getArabicNumber(n: Int): String {
        if (n == 100) return "مئة"
        val units = listOf("", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة")
        val tens = listOf("", "عشرة", "عشرون", "ثلاثون", "أربعون", "خمسون", "ستون", "سبعون", "ثمانون", "تسعون")
        
        if (n < 10) return units[n]
        if (n == 10) return "عشرة"
        if (n == 11) return "أحد عشر"
        if (n == 12) return "اثنا عشر"
        if (n in 13..19) {
            return "${units[n % 10]} عشر"
        }
        val t = n / 10
        val u = n % 10
        return if (u == 0) {
            tens[t]
        } else {
            "${units[u]} و${tens[t]}"
        }
    }

    private fun formatNumberForNoun(numberSpanish: String, isFeminine: Boolean): String {
        if (numberSpanish == "uno") {
            return if (isFeminine) "una" else "un"
        }
        if (numberSpanish.endsWith(" y uno")) {
            return if (isFeminine) {
                numberSpanish.replace(" y uno", " y una")
            } else {
                numberSpanish.replace(" y uno", " y un")
            }
        }
        if (numberSpanish == "veintiuno") {
            return if (isFeminine) "veintiuna" else "veintiún"
        }
        return numberSpanish
    }

    fun getNumberExample(n: Int): Pair<String, String> {
        val esNum = getSpanishNumber(n)
        val arNum = getArabicNumber(n)
        
        return when (n % 5) {
            1 -> { // Scenario 1: Apples (feminine)
                if (n == 1) {
                    Pair("Compré una manzana.", "اشتريt تفاحة واحدة.") // wait, typo: "اشتريت تفاحة واحدة."
                } else if (n == 2) {
                    Pair("Compré dos manzanas.", "اشتريت تفاحتين.")
                } else {
                    val esFormatted = formatNumberForNoun(esNum, isFeminine = true)
                    val arText = when {
                        n in 3..10 -> "$arNum تفاحات"
                        else -> "$arNum تفاحة"
                    }
                    Pair("Compré $esFormatted manzanas.", "اشتريت $arText.")
                }
            }
            2 -> { // Scenario 2: Books (masculine)
                if (n == 1) {
                    Pair("Leo un libro.", "أقرأ كتاباً واحداً.")
                } else if (n == 2) {
                    Pair("Leo dos libros.", "أقرأ كتابين.")
                } else {
                    val esFormatted = formatNumberForNoun(esNum, isFeminine = false)
                    val arText = when {
                        n in 3..10 -> "$arNum كتب"
                        n in 11..99 -> "$arNum كتاباً"
                        else -> "$arNum كتاب"
                    }
                    Pair("Leo $esFormatted libros.", "أقرأ $arText.")
                }
            }
            3 -> { // Scenario 3: Years / Age
                if (n == 1) {
                    Pair("Mi hermano tiene un año.", "عمر أخي سنة واحدة.")
                } else if (n == 2) {
                    Pair("Mi hermano tiene dos años.", "عمر أخي سنتان.")
                } else {
                    val esFormatted = formatNumberForNoun(esNum, isFeminine = false)
                    val arText = when {
                        n in 3..10 -> "$arNum سنوات"
                        else -> "$arNum سنة"
                    }
                    Pair("Mi hermano tiene $esFormatted años.", "عمر أخي $arText.")
                }
            }
            4 -> { // Scenario 4: Euros
                if (n == 1) {
                    Pair("Esto cuesta un euro.", "هذا يكلف يورواً واحداً.")
                } else if (n == 2) {
                    Pair("Esto cuesta dos euros.", "هذا يكلف يورويْن.")
                } else {
                    val esFormatted = formatNumberForNoun(esNum, isFeminine = false)
                    val arText = when {
                        n in 3..10 -> "$arNum يورو"
                        else -> "$arNum يورو"
                    }
                    Pair("Esto cuesta $esFormatted euros.", "هذا يكلف $arText.")
                }
            }
            else -> { // Scenario 5: Days (n % 5 == 0)
                if (n == 1) {
                    Pair("Estaremos allí un día.", "سنبقى هناك يوماً واحداً.")
                } else if (n == 2) {
                    Pair("Estaremos allí dos días.", "سنبقى هناك يومين.")
                } else {
                    val esFormatted = formatNumberForNoun(esNum, isFeminine = false)
                    val arText = when {
                        n in 3..10 -> "$arNum أيام"
                        n in 11..99 -> "$arNum يوماً"
                        else -> "$arNum يوم"
                    }
                    Pair("Estaremos allí $esFormatted días.", "سنبقى هناك $arText.")
                }
            }
        }
    }

    fun generateCustomNumbers(from: Int, to: Int, targetLang: String = "es"): List<Sentence> {
        val list = mutableListOf<Sentence>()
        val minVal = from.coerceIn(1, 100)
        val maxVal = to.coerceIn(minVal, 100)
        
        var idCounter = -100000 // Negative IDs to prevent DB conflicts
        
        for (n in minVal..maxVal) {
            val esWord = getSpanishNumber(n)
            val arWord = getArabicNumber(n)
            val esWordCapitalized = esWord.replaceFirstChar { it.uppercase() }
            
            // 1. Word card
            list.add(
                Sentence(
                    id = idCounter--,
                    text = esWordCapitalized,
                    translation = arWord,
                    targetLang = targetLang,
                    sourceLang = "ar",
                    category = "CustomNumbers",
                    contentType = "word"
                )
            )
            
            // 2. Example sentence card
            val (esExample, arExample) = getNumberExample(n)
            list.add(
                Sentence(
                    id = idCounter--,
                    text = esExample,
                    translation = arExample,
                    targetLang = targetLang,
                    sourceLang = "ar",
                    category = "CustomNumbers",
                    contentType = "sentence"
                )
            )
        }
        return list
    }
}
