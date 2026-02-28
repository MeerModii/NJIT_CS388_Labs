package com.example.lab_5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Headers
import org.json.JSONException

private const val TAG = "MainActivity"
private const val PARKS_API_KEY = BuildConfig.API_KEY
private const val CAMPGROUND_URL =
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

        // Load new items from our database
        lifecycleScope.launch {
            (application as CampgroundApplication).db.campgroundDao().getAll().collect { databaseList ->
                databaseList.map { entity ->
                    Campground(
                        entity.name,
                        entity.description,
                        entity.latLong,
                        listOf(CampgroundImage(entity.imageUrl, null))
                    )
                }.also { mappedList ->
                    campgrounds.clear()
                    campgrounds.addAll(mappedList)
                    campgroundAdapter.notifyDataSetChanged()
                }
            }
        }

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
                        lifecycleScope.launch(IO) {
                            (application as CampgroundApplication).db.campgroundDao().deleteAll()
                            (application as CampgroundApplication).db.campgroundDao().insertAll(list.map {
                                CampgroundEntity(
                                    name = it.name,
                                    description = it.description,
                                    latLong = it.latLong,
                                    imageUrl = it.imageUrl
                                )
                            })
                        }
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
