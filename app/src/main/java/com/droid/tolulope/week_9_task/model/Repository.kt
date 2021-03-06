package com.droid.tolulope.week_9_task.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droid.tolulope.week_9_task.model.network.JsonPlaceholderClient
import com.droid.tolulope.week_9_task.model.network.JsonPlaceholderEndpoint
import com.droid.tolulope.week_9_task.utils.accessCallback
import com.droid.tolulope.week_9_task.utils.accessCallbackList

/**
 * This repository class abstracts access to the API endpoint, provides a clean API for
 * data access to the rest of the application, manages queries and communicates with the
 * remote data source according to request from the HomepageViewModel
 */
class Repository {

    private var jsonPlaceholderEndpoint: JsonPlaceholderEndpoint? = null

    // Initializes the endpoint which in turn creates the interface that relates with the network
    init {
        jsonPlaceholderEndpoint = JsonPlaceholderClient.getPlaceholderEndPoint()
    }

    /*
     Retrieves all the post from the network and saves it in a list of liveData
     which contains all the post
     */
    fun getAllPosts(): LiveData<List<Post>?> {
        val post = MutableLiveData<List<Post>?>()
        jsonPlaceholderEndpoint?.getAllPosts()?.enqueue(accessCallbackList(post))
        return post
    }

    /*
     Asynchronously retrieves all the photos from the network and saves it in a list of liveData
     which contains all the photos
     */
    fun getAllPhotos(): LiveData<List<Photos>?> {
        val photos = MutableLiveData<List<Photos>?>()
        jsonPlaceholderEndpoint?.getAllPhotos()?.enqueue(accessCallbackList(photos))
        return photos
    }

    // Gets comments for a post from the api based on the Id received
    fun getCommentForId(id: Int): LiveData<List<Comment>?> {
        val comment = MutableLiveData<List<Comment>?>()
        jsonPlaceholderEndpoint?.getCommentForId(id)?.enqueue(accessCallbackList(comment))
        return comment
    }

    /*
    Receives created post and posts it to the server, listens for the response and
    on success adds the created post to the posts already on the local database
     */
    fun createNewPost(post: Post): LiveData<Post> {
        val createdPost = MutableLiveData<Post>()
        jsonPlaceholderEndpoint?.createNewPost(post)?.enqueue(accessCallback(createdPost))
        return createdPost
    }

    /*
    Creates a new comment, posts it to the api and retrieves the comment in order to display it
    in the recycler view
    */
    fun addNewComment(comment: Comment, id: Int): LiveData<Comment> {
        val newComment = MutableLiveData<Comment>()
        jsonPlaceholderEndpoint?.addNewComment(id, comment)?.enqueue(accessCallback(newComment))
        return newComment
    }


}