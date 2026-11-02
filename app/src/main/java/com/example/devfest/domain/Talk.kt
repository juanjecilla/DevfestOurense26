package com.example.devfest.domain

import java.time.LocalTime

/** A session of the agenda (talk, demo, break…). Data: see [com.example.devfest.data.TalkCatalog]. */
data class Talk(
    val id: String,
    val title: String,
    /** Null for breaks and unconfirmed slots. */
    val speaker: String?,
    val track: String,
    val start: LocalTime,
    val room: String,
)
