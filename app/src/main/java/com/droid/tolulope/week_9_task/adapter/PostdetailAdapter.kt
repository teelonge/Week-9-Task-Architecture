package com.droid.tolulope.week_9_task.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.droid.tolulope.week_9_task.R
import com.droid.tolulope.week_9_task.model.Comment

/**
 * An adapter that binds comments received from the network call into a recycler view for efficient display of the
 * items in a view. It creates and binds the view for the email and name of the user commenting and the comment body.
 */

class PostdetailAdapter(private val context: Context) :
    RecyclerView.Adapter<PostdetailAdapter.CommentViewHolder>() {

    // This will hold the list of comments from the network call
    private var comments: MutableList<Comment>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_detail_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        comments?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount() = comments?.size ?: 0

    // ViewHolder class for the recyclerView comments
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtCommentNameEmail = itemView.findViewById<TextView>(R.id.txtCommentNameEmail)
        private val txtCommentBody = itemView.findViewById<TextView>(R.id.txtCommentBody)

        fun bind(comment: Comment) {
            // Sets the text of the comment body
            txtCommentBody.text = comment.body

            // Sets the name and email text for the user that is commenting
            txtCommentNameEmail.text = HtmlCompat.fromHtml(
                context.getString(
                    R.string.placeholder_comment_name_email,
                    comment.name,
                    comment.email
                ), HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }

    /*
    Receives the comments retrieved from the network call and sets it
    up here in the adapter
     */
    fun setUpComments(comments: List<Comment>?) {
        this.comments = comments as MutableList<Comment>
        notifyDataSetChanged()
    }

    /*
    Gets the newly created comment from the network and add
    it to the list of comment already present in the recycler view
     */
    fun addNewComment(comment: Comment) {
        comments?.add(comment)
        notifyDataSetChanged()
    }
}