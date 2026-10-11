package com.example.devfest.domain

import java.time.LocalTime

/** A talk in the agenda. Sample data: see [com.example.devfest.data.TalkCatalog]. */
data class Talk(
    val id: String,
    val title: String,
    val track: String,
    val start: LocalTime,
    val durationMinutes: Int,
    val room: String,
)
