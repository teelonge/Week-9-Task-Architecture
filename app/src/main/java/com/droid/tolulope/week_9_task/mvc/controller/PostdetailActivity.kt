package com.droid.tolulope.week_9_task.mvc.controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.droid.tolulope.week_9_task.R
import com.droid.tolulope.week_9_task.adapter.PostdetailAdapter
import com.droid.tolulope.week_9_task.model.Comment
import com.droid.tolulope.week_9_task.model.Repository
import com.droid.tolulope.week_9_task.mvvm.ViewModelFactory
import com.droid.tolulope.week_9_task.mvvm.viewmodel.PostdetailViewModel
import com.droid.tolulope.week_9_task.utils.showToast

/**
 * This activity displays each post details which includes, the post's title and body
 * and list all the comments under the post, also as a button to add a comment under
 * post
 */
class PostdetailActivity : AppCompatActivity() {
    private lateinit var viewModel: PostdetailViewModel
    private lateinit var postdetailAdapter: PostdetailAdapter
    private lateinit var postdetailRecycler: RecyclerView
    private lateinit var txtPostdetailTitle: TextView
    private lateinit var txtPostdetailBody: TextView
    private lateinit var edtNewComment: EditText
    private lateinit var btnPostNewComment: Button
    private var comments = arrayListOf<Comment>()
    private var repository: Repository? = null

    // Will hold the postId gotten from the homepage which is used to load the comments
    private var postId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postdetail)


        val repository = Repository()

        // Initializes the homepageViewModel
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PostdetailViewModel::class.java)


        // Initializes the view in the activity and also sets up the adapter
        initViews()

        // Retrieves the post id, postTitle and postBody
        postId = intent.getIntExtra("postId", 0)
        val postTitle = intent.getStringExtra("postTitle")
        val postBody = intent.getStringExtra("postBody")

        // Sets the postTitle and body to their respective fields
        txtPostdetailTitle.text = postTitle
        txtPostdetailBody.text = postBody

        // Calls this method to retrieve comments specific to the postId and handles configuration change in order not to recall network
//        if (savedInstanceState == null) {
//            viewModel.getComments(postId)
//        }

        repository.getCommentForId(postId).observe(this,{
            comments = it as ArrayList<Comment>
            postdetailAdapter.setUpComments(comments)
        })

        // Creates a new comment on clicking
        btnPostNewComment.setOnClickListener {
            newComment()
            edtNewComment.text.clear()
        }
    }


    private fun newComment() {
        var commentBody = ""
        val commenterName = "Tolulope Longe"
        val commenterEmail = "tolulope_longe732@gmail.com"

        commentBody = edtNewComment.text.toString()
        if (commentBody.isNotEmpty()) {
            // Creates the comment and sends it to the viewmodel with postId to post the comment to the api
            val comment = Comment(body = commentBody,name = commenterName,email = commenterEmail, id = postId)
            repository?.addNewComment(comment, postId)?.observe(this, {
                postdetailAdapter.addNewComment(it)
                postdetailRecycler.smoothScrollToPosition(postdetailAdapter.itemCount-1)
            })
        } else {
            showToast(this,"Type in your comment")
        }
    }

    private fun initViews() {
        txtPostdetailTitle = findViewById(R.id.txtPostDetailTitle)
        txtPostdetailBody = findViewById(R.id.txtPostDetailBody)
        postdetailRecycler = findViewById(R.id.postDetailRecycler)
        edtNewComment = findViewById(R.id.edtNewComment)
        btnPostNewComment = findViewById(R.id.btnNewComment)
        postdetailAdapter = PostdetailAdapter(this)
        postdetailRecycler.adapter = postdetailAdapter
    }
}