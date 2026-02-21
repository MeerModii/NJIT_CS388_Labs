package com.example.lab_4

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.serialization.json.Json
import okhttp3.Headers
import org.json.JSONException

private const val TAG = "MainActivity"
private val PARKS_API_KEY = BuildConfig.API_KEY
private val CAMPGROUND_URL =
    "https://developer.nps.gov/api/v1/campgrounds?api_key=${PARKS_API_KEY}"

class MainActivity : AppCompatActivity() {
    private val campgrounds = mutableListOf<Campground>()
    private lateinit var campgroundsRecyclerView: RecyclerView
    private lateinit var campgroundAdapter: CampgroundAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        campgroundsRecyclerView = findViewById(R.id.campgroundsRecyclerView)
        campgroundAdapter = CampgroundAdapter(this, campgrounds)
        campgroundsRecyclerView.adapter = campgroundAdapter

        val client = AsyncHttpClient()
        client.get(CAMPGROUND_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch campgrounds: $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "Successfully fetched campgrounds: $json")
                try {
                    val parsedJson = createJson().decodeFromString(
                        CampgroundResponse.serializer(),
                        json.jsonObject.toString()
                    )

                    parsedJson.data?.let { list ->
                        campgrounds.addAll(list)
                        campgroundAdapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Exception: $e")
                }
            }
        })
    }

    private fun createJson() = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
    }
}
