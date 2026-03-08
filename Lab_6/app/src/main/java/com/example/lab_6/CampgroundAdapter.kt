package com.example.lab_6

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CampgroundAdapter(private val context: Context, private val campgrounds: List<Campground>) :
    RecyclerView.Adapter<CampgroundAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_campground, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val campground = campgrounds[position]
        holder.bind(campground)
    }

    override fun getItemCount() = campgrounds.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val campgroundImage = itemView.findViewById<ImageView>(R.id.campgroundImage)
        private val campgroundName = itemView.findViewById<TextView>(R.id.campgroundName)
        private val campgroundLocation = itemView.findViewById<TextView>(R.id.campgroundLocation)
        private val campgroundDescription = itemView.findViewById<TextView>(R.id.campgroundDescription)

        fun bind(campground: Campground) {
            campgroundName.text = campground.name
            campgroundLocation.text = campground.latLong
            campgroundDescription.text = campground.description

            Glide.with(context)
                .load(campground.imageUrl)
                .centerCrop()
                .into(campgroundImage)
        }
    }
}