package com.example.lab_3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Headers
import org.json.JSONArray

private const val API_KEY = "y3fWlsDpeO89A7KiJb6uXb0PMdhySy6FIYUJA0wh"

class NationalParksFragment : Fragment(), OnListFragmentInteractionListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_national_parks_list, container, false)
        val progressBar = view.findViewById<View>(R.id.progress) as ContentLoadingProgressBar
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        updateAdapter(progressBar, recyclerView)
        return view
    }

    private fun updateAdapter(progressBar: ContentLoadingProgressBar, recyclerView: RecyclerView) {
        progressBar.show()

        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api_key"] = API_KEY
        // Optional: Get more parks by setting a limit
        params["limit"] = "20"

        client["https://developer.nps.gov/api/v1/parks", params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                progressBar.hide()

                try {
                    val dataJSON = json.jsonObject.get("data") as JSONArray
                    val parksRawJSON = dataJSON.toString()

                    val gson = Gson()
                    val arrayParkType = object : TypeToken<List<NationalPark>>() {}.type

                    val models: List<NationalPark> = gson.fromJson(parksRawJSON, arrayParkType)

                    if (models.isEmpty()) {
                        Toast.makeText(context, "No parks found", Toast.LENGTH_SHORT).show()
                    } else {
                        recyclerView.adapter = NationalParksRecyclerViewAdapter(models, this@NationalParksFragment)
                        Log.d("NationalParksFragment", "Successfully loaded ${models.size} parks")
                    }
                } catch (e: Exception) {
                    Log.e("NationalParksFragment", "Parsing error: ${e.message}")
                    Toast.makeText(context, "Failed to parse data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                progressBar.hide()
                val message = "API Error: $statusCode"
                Log.e("NationalParksFragment", "$message - $errorResponse")
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }]
    }

    override fun onItemClick(item: NationalPark) {
        Log.d("NationalParksFragment", "Clicked on ${item.name}")
    }
}
