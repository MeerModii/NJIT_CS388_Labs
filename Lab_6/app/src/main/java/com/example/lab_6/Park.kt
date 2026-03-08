package com.example.lab_6

import android.support.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ParksResponse(
    @SerialName("data")
    val data: List<Park>?
)

@Keep
@Serializable
data class Park(
    @SerialName("fullName")
    val fullName: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("images")
    val images: List<ParkImage>?
)

@Keep
@Serializable
data class ParkImage(
    @SerialName("url")
    val url: String?
)