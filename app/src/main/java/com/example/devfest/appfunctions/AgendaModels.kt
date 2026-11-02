package com.example.devfest.appfunctions

import androidx.appfunctions.AppFunctionSerializable

/** A session of the DevFest Ourense agenda: a talk, a startup demo or a break. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class Talk(
    /** Stable talk ID. Pass it to "addToFavorites". */
    val id: String,
    /** Session title, in the language the organisers published it. */
    val title: String,
    /** Speaker name. Null for breaks and unconfirmed slots. */
    val speaker: String?,
    /** Track in lowercase: "ai", "android", "web", "demo" or "general". */
    val track: String,
    /** Start time as HH:mm, local to the event. */
    val start: String,
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
