package com.droid.tolulope.week_9_task.model.network

import com.droid.tolulope.week_9_task.model.Post
import com.droid.tolulope.week_9_task.model.Comment
import com.droid.tolulope.week_9_task.model.Photos
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JsonPlaceholderEndpoint {

    // Calls the api to retreive all the posts
    @GET("posts")
    fun getAllPosts(): Call<List<Post>>

    // Retrieves all the photos from the api
    @GET("photos")
    fun getAllPhotos(): Call<List<Photos>>

    // gets the list of comment based on the user id
    @GET("posts/{id}/comments")
    fun getCommentForId(@Path("id") id: Int): Call<List<Comment>>

    // Uploads newly created post to the api
    @POST("posts")
    fun createNewPost(@Body post: Post): Call<Post>

    // Uploads newly added cmment to the api
    @POST("posts/{id}/comments")
    fun addNewComment(@Path("id") id: Int, @Body comment: Comment): Call<Comment>
}