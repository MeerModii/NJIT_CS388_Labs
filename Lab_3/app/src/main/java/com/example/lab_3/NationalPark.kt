package com.example.lab_3

import com.google.gson.annotations.SerializedName

class NationalPark {
    @JvmField
    @SerializedName("fullName")
    var name: String? = null

    @JvmField
    @SerializedName("description")
    var description: String? = null

    @JvmField
    @SerializedName("states")
    var location: String? = null

    @SerializedName("images")
    var images: List<Image>? = null

    // Convenience property to access the first image’s URL
    val imageUrl: String? get() = images?.firstOrNull()?.url

    class Image {
        @SerializedName("url")
        var url: String? = null
    }
}
