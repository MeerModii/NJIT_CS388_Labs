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
import org.json.JSONException

private const val TAG = "ParksFragment"

class ParksFragment : Fragment() {
    private val parks = mutableListOf<Park>()
    private lateinit var parksRecyclerView: RecyclerView
    private lateinit var parksAdapter: ParksAdapter
    private lateinit var loadingText: TextView
    
    // Using the same URL format as your previous successful lab
    private val API_KEY = BuildConfig.API_KEY
    private val PARKS_URL = "https://developer.nps.gov/api/v1/parks?api_key=${API_KEY}&limit=20"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parks, container, false)

        val layoutManager = LinearLayoutManager(context)
        parksRecyclerView = view.findViewById(R.id.parks)
        parksRecyclerView.layoutManager = layoutManager
        parksRecyclerView.setHasFixedSize(true)
        parksAdapter = ParksAdapter(view.context, parks)
        parksRecyclerView.adapter = parksAdapter

        loadingText = view.findViewById(R.id.loadingText)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Safety check for the API Key
        if (API_KEY == "null" || API_KEY.isEmpty()) {
            loadingText.text = "Error: API Key not found in apikey.properties"
            return
        }
        
        fetchParks()
    }

    private fun fetchParks() {
        val client = AsyncHttpClient()
        
        client.get(PARKS_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch parks: $statusCode. Response: $response")
                // Show more info if available
                val errorMsg = if (response?.contains("Invalid") == true) "Invalid API Key" else "Server Error: $statusCode"
                loadingText.text = errorMsg
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.i(TAG, "Successfully fetched parks: $json")
                try {
                    val parsedJson = createJson().decodeFromString(
                        ParksResponse.serializer(),
                        json.jsonObject.toString()
                    )
                    parsedJson.data?.let { list ->
                        parks.clear()
                        parks.addAll(list)
                        parksAdapter.notifyDataSetChanged()
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