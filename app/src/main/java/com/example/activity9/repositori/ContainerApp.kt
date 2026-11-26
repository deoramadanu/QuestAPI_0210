package com.example.activity9.repositori

import android.app.Application
import com.example.activity9.apiservice.ServiceApiSiswa
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

interface ContainerApp {
    val repositoryDataSiswa: RepositoryDataSiswa
}

class DefaultContainerApp : ContainerApp {
    private val baseurl = "http://10.69.14.19/tiumy/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val klien = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseurl)
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            }.asConverterFactory("application/json".toMediaType())
        )
        .client(klien)
        .build()

    private val retrofitService: ServiceApiSiswa by lazy {
        retrofit.create(ServiceApiSiswa::class.java)
    }

    override val repositoryDataSiswa: RepositoryDataSiswa by lazy {
        JaringanRepositoryDataSiswa(retrofitService)
    }
}

class AplikasiDataSiswa : Application() {
    lateinit var container: ContainerApp
    override fun onCreate() {
        super.onCreate()
        this.container = DefaultContainerApp()
    }
}