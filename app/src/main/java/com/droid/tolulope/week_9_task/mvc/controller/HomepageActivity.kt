package com.droid.tolulope.week_9_task.mvc.controller

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.droid.tolulope.week_9_task.R
import com.droid.tolulope.week_9_task.adapter.HomepageAdapter
import com.droid.tolulope.week_9_task.model.Photos
import com.droid.tolulope.week_9_task.model.Post
import com.droid.tolulope.week_9_task.model.Repository
import com.droid.tolulope.week_9_task.utils.RecyclerViewClickListener
import com.droid.tolulope.week_9_task.utils.queryAllPosts
import com.droid.tolulope.week_9_task.utils.showToast
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * HomepageActivity which handles all the views for the homepage. Holds the recycler view
 * that is used to display all the posts retrieved from the api, implements the recycler
 * view clickListener which handles click events of items in the recycler view
 */
class HomepageActivity : AppCompatActivity(), RecyclerViewClickListener{
    private lateinit var homeAdapter: HomepageAdapter
    private lateinit var postRecycler: RecyclerView
    private lateinit var createPost: FloatingActionButton
    private lateinit var searchView: SearchView
    private lateinit var postProgressLoader: ProgressBar
    private lateinit var backgroundAnim : AnimationDrawable
    private var posts = arrayListOf<Post>()
    private var photos = arrayListOf<Photos>()


   private lateinit var repository : Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ConstraintLayout>(R.id.homeBackground).apply {
            setBackgroundResource(R.drawable.home_background)
            backgroundAnim = background as AnimationDrawable
        }
        backgroundAnim.start()

        repository = Repository()

        // Initializes necessary views and sets up the adapters
        initViews()
        repository.getAllPosts().observe(this,{
            posts = it as ArrayList<Post>
            homeAdapter.setupPosts(posts)
        })
        repository.getAllPhotos().observe(this,{
            if (it != null){
                photos = it as ArrayList<Photos>
                homeAdapter.setupPhotos(photos)
                postRecycler.visibility = View.VISIBLE
            }
            postProgressLoader.visibility = View.GONE

        })

        /*
         * This floating action button when clicked opens up a dialog which handles creating a new post
         * which later on gets add to the local list and remote api
         */
        createPost.setOnClickListener {
            displayNewPostDialog()
        }
    }

    // Handles the click listener for each items in the homepageActivity
    override fun onRecyclerViewItemClicked(view: View, post: Post) {
        when (view.id) {
            R.id.btnComment -> {
                val intent = Intent(this, PostdetailActivity::class.java)
                intent.putExtra("postId", post.id)
                intent.putExtra("postTitle", post.title)
                intent.putExtra("postBody", post.body)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "postTitle")
                        .toBundle()
                )
            }
        }
    }

    private fun initViews() {
        homeAdapter = HomepageAdapter(this)
        homeAdapter.listener = this
        postRecycler = findViewById(R.id.postRecycler)
        createPost = findViewById(R.id.createNewPost)
        postProgressLoader = findViewById(R.id.postProgressLoader)
        postRecycler.adapter = homeAdapter
    }

    /*
     * Creates a new dialog for creating a new post and observes if post has been created, if so adds
     * it to the recycler view
     */
    private fun displayNewPostDialog() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.new_post_dialog, null)
        dialog.setContentView(view)

        var postTitle = ""
        var postBody = ""
        val edtNewPostTitle: EditText = view.findViewById(R.id.edtNewPostTitle)
        val edtNewPostBody: EditText = view.findViewById(R.id.edtNewPostBody)
        val btnCreate = view.findViewById<Button>(R.id.btnCreatePost)

        btnCreate.setOnClickListener {
            postTitle = edtNewPostTitle.text.toString()
            postBody = edtNewPostBody.text.toString()

            if (postBody.isNotEmpty() && postTitle.isNotEmpty()) {
                val post = Post(userId = 11, id = 109, title = postTitle, body = postBody)
                repository.createNewPost(post).observe(this,{
                    homeAdapter.addNewlyCreatedPost(post)
                    postRecycler.smoothScrollToPosition(0)
                })

                    dialog.cancel()
            } else {
                showToast(this, "Enter post details correctly")
            }
        }
        dialog.show()
        val window = dialog.window
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

    }

    /*
     * Creates optionMenu and handles its item based on selection, makes query based on
     * changes in edtTextValue
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.homepage_menu, menu)
        val searchItem = menu?.findItem(R.id.searchPostByTitle)

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        queryAllPosts(homeAdapter, searchView)
        return super.onCreateOptionsMenu(menu)
    }


}