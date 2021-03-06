package com.droid.tolulope.week_9_task.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droid.tolulope.week_9_task.model.Repository
import com.droid.tolulope.week_9_task.model.Comment

/**
 * PostdetailViewModel uses the repository to make calls to the network to retrieve comments based
 * on a particular post id and also to create a new comment
 */
class PostdetailViewModel(private val repository: Repository) : ViewModel() {

    // LiveData that holds the value for all the post from the network
    private var _commentsPerId = MutableLiveData<List<Comment>?>()
    val commentsPerId: LiveData<List<Comment>?>
        get() = _commentsPerId

    // This liveData holds the value of the new comment received as a response from the network call
    private var _newComment = MutableLiveData<Comment>()
    val newComment: LiveData<Comment>
        get() = _newComment


    /*
     Gets all post from the remote source using the repository and saves it in a mutableLiveData
     in which a liveData saves it and its observed from the homepageView for changes
     */
    fun getComments(id: Int) {
        _commentsPerId = repository.getCommentForId(id) as MutableLiveData<List<Comment>?>
    }

    /*
     * Posts a new comment to the API and retrieves the response which is observed by the view for
     * changes and updated in the recycler view accordingly
     */
    fun addNewComment(id: Int, comment: Comment) {
        _newComment = repository.addNewComment(comment, id) as MutableLiveData<Comment>
    }
}