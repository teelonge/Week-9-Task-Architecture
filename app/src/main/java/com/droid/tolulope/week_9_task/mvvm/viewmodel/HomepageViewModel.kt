package com.droid.tolulope.week_9_task.mvvm.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droid.tolulope.week_9_task.model.Photos
import com.droid.tolulope.week_9_task.model.Repository
import com.droid.tolulope.week_9_task.model.Post

/**
 * This viewModel serves as a bridge between the homepageView and the business logic which is the
 * model
 */

class HomepageViewModel(private val repository: Repository) : ViewModel() {

    // LiveData that holds the value for all the post from the network
    private var _postList = MutableLiveData<List<Post>?>()
    val postList: LiveData<List<Post>?>
        get() = _postList

    // LiveData that holds the value for the newly created post gotten from the network
    private var _newPost = MutableLiveData<Post>()
    val newPost: LiveData<Post>
        get() = _newPost

    // LiveData that holds the value for all the photos from the network
    private var _photosList = MutableLiveData<List<Photos>?>()
    val photosList: LiveData<List<Photos>?>
        get() = _photosList


    private var _filteredPostById = MutableLiveData<List<Post>?>()
    val filterPostById: LiveData<List<Post>?>
        get() = _filteredPostById


    /*
     * Gets all post from the remote source using the repository and saves it in a mutableLiveData
     * in which a liveData saves it and its observed from the homepage for changes
     */
    fun getAllPost() {
        _postList = repository.getAllPosts() as MutableLiveData<List<Post>?>

    }

    /*
     * Creates a new post and retrieves this post as a mutableLiveData, saves it a liveData which is
     * observed in the homepage activity
     */
    fun createNewPost(post: Post) {
        _newPost = repository.createNewPost(post) as MutableLiveData<Post>
    }

    /*
     * Gets all photos from the remote source using the repository and saves it in a mutableLiveData
     * in which a liveData saves it and its observed from the homepage for changes
     */
    fun getAllPhotos() {
        _photosList = repository.getAllPhotos() as MutableLiveData<List<Photos>?>
    }


    fun filterPost(){
        _filteredPostById.value = _postList.value?.filter { it.userId ==  1  }
    }


}