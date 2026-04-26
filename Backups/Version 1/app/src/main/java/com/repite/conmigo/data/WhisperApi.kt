package com.repite.conmigo.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface WhisperApi {
    @Multipart
    @POST("v1/audio/transcriptions")
    suspend fun transcribe(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody = RequestBody.create(MultipartBody.FORM, "whisper-1"),
        @Part("language") language: RequestBody? = null
    ): Response<WhisperResponse>
}

data class WhisperResponse(val text: String)
