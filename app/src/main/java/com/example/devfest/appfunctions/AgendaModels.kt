package com.example.devfest.appfunctions

import androidx.appfunctions.AppFunctionSerializable
import java.time.LocalTime

/** A talk. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class Talk(
    /** Id. */
    val id: String,
    /** Title. */
    val title: String,
    /** Track. */
    val track: String,
    /** Start. */
    val start: LocalTime,
    /** Room. */
    val room: String,
)

/** Result. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class FavouritesResult(
    /** Added. */
    val added: Int,
    /** Total. */
    val total: Int,
)
