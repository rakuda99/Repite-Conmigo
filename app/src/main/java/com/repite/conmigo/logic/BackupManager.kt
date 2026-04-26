package com.repite.conmigo.logic

import android.content.Context
import android.os.Environment
import com.repite.conmigo.data.UserProgress
import org.json.JSONObject
import java.io.File

class BackupManager(private val context: Context) {

    fun backupProgress(progress: UserProgress): String {
        return try {
            val json = JSONObject().apply {
                put("xp", progress.xp)
                put("streak", progress.streak)
                put("lastActiveDate", progress.lastActiveDate)
                put("highestAccuracy", progress.highestAccuracy.toDouble())
                put("sessionCount", progress.sessionCount)
                put("totalWordsLearned", progress.totalWordsLearned)
            }

            val saveDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            if (!saveDir.exists()) saveDir.mkdirs()
            
            val file = File(saveDir, "RepiteConmigo_Backup.json")
            file.writeText(json.toString())
            "تم الحفظ في: ${file.absolutePath} ✅"
        } catch (e: Exception) {
            "خطأ في الحفظ: ${e.localizedMessage}"
        }
    }

    fun restoreProgress(): UserProgress? {
        return try {
            val saveDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
            val file = File(saveDir, "RepiteConmigo_Backup.json")
            if (!file.exists()) return null

            val json = JSONObject(file.readText())
            UserProgress(
                xp = json.optInt("xp", 0),
                streak = json.optInt("streak", 0),
                lastActiveDate = json.optLong("lastActiveDate", 0),
                highestAccuracy = json.optDouble("highestAccuracy", 0.0).toFloat(),
                sessionCount = json.optInt("sessionCount", 0),
                totalWordsLearned = json.optInt("totalWordsLearned", 0)
            )
        } catch (e: Exception) {
            null
        }
    }
}
