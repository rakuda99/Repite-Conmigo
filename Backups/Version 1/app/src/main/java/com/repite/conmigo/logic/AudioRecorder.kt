package com.repite.conmigo.logic

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var lastRecordedFile: File? = null

    fun startRecording() {
        val file = File(context.cacheDir, "user_voice.m4a")
        lastRecordedFile = file
        
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording(): String? {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder = null
        return lastRecordedFile?.absolutePath
    }

    fun playLastRecording() {
        if (lastRecordedFile?.exists() == true) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(lastRecordedFile!!.absolutePath)
                prepare()
                start()
            }
        }
    }
}
