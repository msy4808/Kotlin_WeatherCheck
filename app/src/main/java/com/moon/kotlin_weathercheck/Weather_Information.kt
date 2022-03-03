package com.moon.kotlin_weathercheck

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Weather_Information {
    private val  retrofit = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}