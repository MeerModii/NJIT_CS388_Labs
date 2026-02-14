package com.example.lab_3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
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
        updateAdapter(progressBar, recyclerView)
        return view
    }

    private fun updateAdapter(progressBar: ContentLoadingProgressBar, recyclerView: RecyclerView) {
        progressBar.show()

        val client = AsyncHttpClient()
        val params = RequestParams()
        params["api_key"] = API_KEY

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

                    recyclerView.adapter = NationalParksRecyclerViewAdapter(models, this@NationalParksFragment)

                    Log.d("NationalParksFragment", "response successful")
                } catch (e: Exception) {
                    Log.e("NationalParksFragment", "Parsing error: ${e.message}")
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                progressBar.hide()
                Log.e("NationalParksFragment", errorResponse)
            }
        }]
    }

    override fun onItemClick(item: NationalPark) {
        // Handle item click if needed
        Log.d("NationalParksFragment", "Clicked on ${item.name}")
    }
}
