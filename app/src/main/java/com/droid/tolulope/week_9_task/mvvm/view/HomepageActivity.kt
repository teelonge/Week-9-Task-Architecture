package com.droid.tolulope.week_9_task.mvvm.view

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.droid.tolulope.week_9_task.R
import com.droid.tolulope.week_9_task.adapter.HomepageAdapter
import com.droid.tolulope.week_9_task.mvc.controller.HomepageActivity
import com.droid.tolulope.week_9_task.model.Photos
import com.droid.tolulope.week_9_task.model.Post
import com.droid.tolulope.week_9_task.model.Repository
import com.droid.tolulope.week_9_task.mvvm.ViewModelFactory
import com.droid.tolulope.week_9_task.mvvm.viewmodel.HomepageViewModel
import com.droid.tolulope.week_9_task.utils.RecyclerViewClickListener
import com.droid.tolulope.week_9_task.utils.queryAllPosts
import com.droid.tolulope.week_9_task.utils.showToast
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * HomepageActivity which handles all the views for the homepage. Holds the recycler view
 * that is used to display all the posts retrieved from the api, implements the recycler
 * view clickListener which handles click events of items in the recycler view
 */
class HomepageActivity : AppCompatActivity(), RecyclerViewClickListener {
    private lateinit var homeAdapter: HomepageAdapter
    private lateinit var viewModel: HomepageViewModel
    private lateinit var postRecycler: RecyclerView
    private lateinit var createPost: FloatingActionButton
    private lateinit var searchView: SearchView
    private lateinit var postProgressLoader: ProgressBar
    private lateinit var homeBackground : ConstraintLayout
    private lateinit var backgroundAnim: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ConstraintLayout>(R.id.homeBackground).apply {
            setBackgroundResource(R.drawable.home_background)
            backgroundAnim = background as AnimationDrawable
        }
        backgroundAnim.start()

        val repository = Repository()

        // Initializes the homepageViewModel
        val viewModelFactory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomepageViewModel::class.java)

        // Initializes necessary views and sets up the adapters
        initViews()
        retrievePosts()
        retrievePhotos()
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
                openPostDetail(post, view)
            }
        }
    }

    private fun openPostDetail(
        post: Post,
        view: View
    ) {
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


    private fun initViews() {
        homeAdapter = HomepageAdapter(this)
        homeAdapter.listener = this
        postRecycler = findViewById(R.id.postRecycler)
        createPost = findViewById(R.id.createNewPost)
        postProgressLoader = findViewById(R.id.postProgressLoader)
        homeBackground = findViewById(R.id.homeBackground)
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

                viewModel.createNewPost(post)
                viewModel.newPost.observe(this, {
                    homeAdapter.addNewlyCreatedPost(it)
                    postRecycler.smoothScrollToPosition(0)
                    dialog.cancel()
                })
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                filterPostById()
            }
            R.id.showAll -> retrievePosts()
            R.id.goToMvc -> startActivity(Intent(this,HomepageActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun retrievePosts() {
        // Calls this method to retrieve all the posts
        viewModel.getAllPost()
        /*
       Observes for changes in the list of posts, as soon as data is available, it is sent
       to the adapter which then handles populating the recycler view
       */
        viewModel.postList.observe(this, {
            if (it != null) {
                homeAdapter.setupPosts(it as MutableList<Post>)
            } else {
                showToast(this, "An error occurred")
            }

        })
    }

    private fun retrievePhotos() {
        /*
       ViewModel makes the call to this method to begin the process of making the network
       call to the api to retrieve all photos
       */
        viewModel.getAllPhotos()

        /*
         * Observes for changes in the list of photos, updates the adapter according and populates
         * the UI with said data
         */
        viewModel.photosList.observe(this, Observer {
            if (it != null) {
                homeAdapter.setupPhotos(it as MutableList<Photos>)
                postRecycler.visibility = View.VISIBLE
            } else {
                showToast(this, "An error occurred")
            }
            postProgressLoader.visibility = View.GONE

        })
    }

    private fun filterPostById() {
        viewModel.filterPost()
        viewModel.filterPostById.observe(this, {
            homeAdapter.setupPosts(it as MutableList<Post>)
        })
    }

}