package com.example.devfest.appfunctions

import androidx.appfunctions.AppFunctionSerializable
import java.time.LocalTime

/** A talk in the DevFest Ourense agenda. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class Talk(
    /** Stable talk ID. Pass it to "addToFavorites". */
    val id: String,
    /** Talk title. */
    val title: String,
    /** Track name in lowercase: "android", "web", "cloud", "ai" or "general". */
    val track: String,
    /** Start time, local to the event. */
    val start: LocalTime,
    /** Room name. */
    val room: String,
)

/** Result of changing the user's favourite talks. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class FavouritesResult(
    /** Talks newly added by this call. 0 when all of them were already saved. */
    val added: Int,
    /** Total favourite talks after this call. */
    val total: Int,
)
