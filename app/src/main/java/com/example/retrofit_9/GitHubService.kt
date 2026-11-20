package com.example.retrofit_9

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {
    @GET("users")
    suspend fun users(
        @Query("per_page") perPage: Int = 5
    ): List<User>

    @GET("users/{login}")
    suspend fun user(
        @Path("login") login: String
    ): User
}
