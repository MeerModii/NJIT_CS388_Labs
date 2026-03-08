package com.example.lab_6

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.serialization.json.Json
import okhttp3.Headers

private const val TAG = "CampgroundFragment"

class CampgroundFragment : Fragment() {
    private val campgrounds = mutableListOf<Campground>()
    private lateinit var campgroundsRecyclerView: RecyclerView
    private lateinit var campgroundAdapter: CampgroundAdapter
    private lateinit var loadingText: TextView
    
    private val API_KEY = BuildConfig.API_KEY
    private val CAMPGROUND_URL = "https://developer.nps.gov/api/v1/campgrounds?api_key=${API_KEY}&limit=20"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_campground, container, false)

        val layoutManager = LinearLayoutManager(context)
        campgroundsRecyclerView = view.findViewById(R.id.campgrounds)
        campgroundsRecyclerView.layoutManager = layoutManager
        campgroundsRecyclerView.setHasFixedSize(true)
        campgroundAdapter = CampgroundAdapter(view.context, campgrounds)
        campgroundsRecyclerView.adapter = campgroundAdapter

        loadingText = view.findViewById(R.id.loadingText)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (API_KEY == "null" || API_KEY.isEmpty()) {
            loadingText.text = "Error: API Key not found"
            return
        }
        
        fetchCampgrounds()
    }

    private fun fetchCampgrounds() {
        val client = AsyncHttpClient()
        
        client.get(CAMPGROUND_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch campgrounds: $statusCode. Response: $response")
                loadingText.text = "Server Error: $statusCode"
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.i(TAG, "Successfully fetched campgrounds: $json")
                try {
                    val parsedJson = createJson().decodeFromString(
                        CampgroundResponse.serializer(),
                        json.jsonObject.toString()
                    )
                    parsedJson.data?.let { list ->
                        campgrounds.clear()
                        campgrounds.addAll(list)
                        campgroundAdapter.notifyDataSetChanged()
                        loadingText.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception: $e")
                    loadingText.text = "Data processing error."
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