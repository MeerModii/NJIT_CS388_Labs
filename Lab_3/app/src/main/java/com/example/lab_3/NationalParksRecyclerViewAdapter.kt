package com.example.lab_3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NationalParksRecyclerViewAdapter(
    private val parks: List<NationalPark>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<NationalParksRecyclerViewAdapter.ParkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_national_park, parent, false)
        return ParkViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParkViewHolder, position: Int) {
        val park = parks[position]
        holder.mItem = park
        holder.mParkName.text = park.name
        holder.mParkLocation.text = park.location
        holder.mParkDescription.text = park.description

        val imageUrl = park.imageUrl
        Glide.with(holder.mView)
            .load(imageUrl)
            .centerInside()
            .into(holder.mParkImage)

        holder.mView.setOnClickListener {
            mListener?.onItemClick(park)
        }
    }

    override fun getItemCount(): Int = parks.size

    inner class ParkViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mParkImage: ImageView = mView.findViewById(R.id.park_image)
        val mParkName: TextView = mView.findViewById(R.id.park_name)
        val mParkLocation: TextView = mView.findViewById(R.id.park_location)
        val mParkDescription: TextView = mView.findViewById(R.id.park_description)
        var mItem: NationalPark? = null

        override fun toString(): String {
            return super.toString() + " '" + mParkName.text + "'"
        }
    }
}

interface OnListFragmentInteractionListener {
    fun onItemClick(item: NationalPark)
}
