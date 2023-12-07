package com.example.tawsila

import com.squareup.moshi.Json

data class ParticipationRequest(
    @Json(name = "participationID") val participationID: String,
    @Json(name = "clientID") val clientID: Long,
    @Json(name = "carpoolingID") val carpoolingID: Long,
    @Json(name = "etat") val etat: Int
)