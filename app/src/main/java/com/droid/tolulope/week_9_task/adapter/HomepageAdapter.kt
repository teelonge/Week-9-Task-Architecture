package com.droid.tolulope.week_9_task.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.droid.tolulope.week_9_task.R
import com.droid.tolulope.week_9_task.model.Photos
import com.droid.tolulope.week_9_task.model.Post
import com.droid.tolulope.week_9_task.utils.RecyclerViewClickListener
import com.droid.tolulope.week_9_task.utils.getImages
import com.droid.tolulope.week_9_task.utils.loadImage
import com.droid.tolulope.week_9_task.utils.showToast
import java.util.*


/**
 * An adapter that binds post received from the network call into a recycler view for efficient display of the
 * items in a view. It creates and binds the view for the userId, id ,postTitle, postBody and a postImage
 */

class HomepageAdapter(private val context: Context) :
    RecyclerView.Adapter<HomepageAdapter.HomepageViewHolder>(), Filterable {

    // This will hold the list of posts from the network call
    private var posts: MutableList<Post>? = null

    // Interim hold to save the original list of posts before search operation is performed
    private var ogPosts: MutableList<Post>? = null

    // Initializes the interface for handling click events in the homepage activity
    var listener: RecyclerViewClickListener? = null

    // This will hold the list of photos from the network call
    private var photos: MutableList<Photos>? = null

    // Local images for identifying specific userId
    private var postImages = getImages()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomepageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return HomepageViewHolder(view)
    }


    override fun onBindViewHolder(holder: HomepageViewHolder, position: Int) {
        posts?.get(position)?.let { photos?.get(position)?.let { it1 -> holder.bind(it, it1) } }
    }

    override fun getItemCount() = posts?.size ?: 0

    /**
     * This method constrains data based on a filtering pattern which is the constraint, its operation
     * is performed asynchronously, as the search input comes from the homepage, performs the filter
     * and data that matches the search pattern is added to a new list which is displayed on the UI
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            /*
             Invoked in a worker thread to filter the data according to the constraint.
             */
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchString = constraint.toString()
                if (searchString.isEmpty()) {
                    posts = ogPosts
                } else {
                    posts = ogPosts
                    val tempSearchedPosts = mutableListOf<Post>()
                    for (post in posts!!) {
                        if (post.title?.toLowerCase(Locale.ROOT)?.trim()
                                ?.contains(searchString) == true
                        ) {
                            tempSearchedPosts.add(post)
                        }
                    }
                    posts = tempSearchedPosts
                }

                val filterPosts = FilterResults()
                filterPosts.values = posts
                return filterPosts
            }

            /*
             Invoked in the UI thread to publish the filtering results in the user interface computed in
             performFiltering.
             */
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                posts = results?.values as MutableList<Post>?
                notifyDataSetChanged()
            }

        }
    }

    // ViewHolder class for the recyclerView
    inner class HomepageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtPostTitle = itemView.findViewById<TextView>(R.id.txtPostTitle)
        private val txtPostBody = itemView.findViewById<TextView>(R.id.txtPostBody)
        private val imgPostSmall = itemView.findViewById<ImageView>(R.id.imgPostSmallImage)
        private val imgPostImage = itemView.findViewById<ImageView>(R.id.imgPostImage)
        private val txtUserId = itemView.findViewById<TextView>(R.id.txtUserId)
        private val imgBookmark = itemView.findViewById<ImageView>(R.id.btnBookmark)
        private val btnComment = itemView.findViewById<ImageView>(R.id.btnComment)
        private val btnLike = itemView.findViewById<ImageView>(R.id.btnLike)
        private var collapseIntro = false

        // Bug moved like and bookmarked to data class

        fun bind(post: Post, photo: Photos) {
            btnLike.setOnClickListener {
                when (post.liked) {
                    false -> {
                        btnLike.setImageResource(R.drawable.ic_red_heart)
                        showToast(context,"You liked this post")
                        Log.d("Adapter", "bind: Liked ${post.id}")
                        post.liked = true

                    }
                    true -> {
                        btnLike.setImageResource(R.drawable.heart_svgrepo_com)
                        Log.d("Adapter", "bind: Unlike ${post.id}")
                        post.liked = false
                    }
                }
            }

            // Sets the image for each user
            imgPostSmall.setImageResource(postImages[post.userId!!-1])

            // Sets the post title
            txtPostTitle.text = post.title

            // Sets the post body
            txtPostBody.text = HtmlCompat.fromHtml(
                context.getString(R.string.placeholder_title_body, post.title, post.body
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            // Sets the userId and Id value
            txtUserId.text = context.getString(R.string.placeholder_user_id, post.userId, post.id)

            // Loads the image for each post from the network call using its url and employing glide
            loadImage(context,photo.url,imgPostImage)

            // Sets a clickListener for the cardView which launches the postDetails activity
            btnComment.setOnClickListener {
                listener?.onRecyclerViewItemClicked(it, post)
            }

            // Sets a clickListener on the post body to expand or collapse it
            txtPostBody.setOnClickListener {
                if (collapseIntro) {
                    txtPostBody.maxLines = 3
                    collapseIntro = false
                } else {
                    txtPostBody.maxLines = Int.MAX_VALUE
                    collapseIntro = true
                }
            }

            imgBookmark.setOnClickListener {
                when (post.bookmarked) {
                    false -> {
                        imgBookmark.setImageResource(R.drawable.bookmark_svgrepo_com)
                        showToast(context,"${post.id} added to bookmarks")
                        post.bookmarked = true

                    }
                    true -> {
                        imgBookmark.setImageResource(R.drawable.bookmark_svgrepo_com_2)
                        post.bookmarked = false
                    }
                }

            }
        }
    }

    /*
    Receives the posts retrieved from the network call and sets it
    up here in the adapter
     */
    fun setupPosts(posts: MutableList<Post>) {
        this.posts = posts
        this.ogPosts = posts
        notifyDataSetChanged()
    }

    /*
    Gets the newly created post from the network and add
    it to the list of post already present in the recycler view
     */
    fun addNewlyCreatedPost(post: Post) {
        posts?.add(0, post)
        notifyItemInserted(0)
    }

    /*
    Receives the photos retrieved the network call and sets it
    up here in the adapter
     */
    fun setupPhotos(photos: MutableList<Photos>) {
        this.photos = photos
        notifyDataSetChanged()
    }

}