package com.example.newheadsup

import com.example.newheadsup.model.Celebrity
import retrofit2.Call
import retrofit2.http.GET


interface APIInterface {
    @GET("/celebrities/")
    fun getCelebs(): Call<ArrayList<Celebrity>>?
}