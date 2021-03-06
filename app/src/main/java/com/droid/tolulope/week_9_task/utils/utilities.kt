package com.droid.tolulope.week_9_task.utils

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.droid.tolulope.week_9_task.R
import com.droid.tolulope.week_9_task.adapter.HomepageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Holds a list of local images used to represent a particular user
fun getImages(): MutableList<Int> {
    return mutableListOf(R.drawable.jack, R.drawable.danearys, R.drawable.oliver,
            R.drawable.carlsen_opt, R.drawable.naomi, R.drawable.jack,
            R.drawable.homeland, R.drawable.anthony, R.drawable.lebron, R.drawable.hermionedhface,R.drawable.serena)
}

/**
 * Loads an image into a given imageView, generates a url using GlideUrl from the given image url
 * and glide then loads the image into the view
 */
fun loadImage(context: Context, imgUrl: String, view: ImageView) {
    val url = GlideUrl(
        imgUrl, LazyHeaders.Builder()
            .addHeader("User-Agent", "your-user-agent")
            .build()
    )
    Glide.with(context)
        .load(url).apply {
            RequestOptions()
                .placeholder(R.drawable.loading_status_animation)
                .error(R.drawable.ic_error_image)
        }
        .into(view)
}

/**
 * A generic helper function that returns a Callback List of items from a particular network request
 * @param element MutableLiveData that holds the value of the response body
 */
fun <T> accessCallbackList(element: MutableLiveData<List<T>?>): Callback<List<T>> {
    return object : Callback<List<T>> {
        override fun onResponse(call: Call<List<T>>, response: Response<List<T>>) {
            if (response.isSuccessful) {
                element.value = response.body()
            } else {
                element.value = null
            }
        }

        override fun onFailure(call: Call<List<T>>, t: Throwable) {
            element.value = null
        }

    }
}

/**
 * A generic helper function that returns a Callback item from a particular network request
 * @param element MutableLiveData that holds the value of the response body
 */
fun <T> accessCallback(element: MutableLiveData<T>): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                element.value = response.body()
            } else {
                element.value = null
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            element.value = null
        }

    }
}

// Displays a toast based on a given message
fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun queryAllPosts(adapter : HomepageAdapter, searchView: SearchView){

    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            adapter.filter.filter(newText)
            return true
        }
    })
}