package com.example.devfest.data

import com.example.devfest.domain.Talk
import java.time.LocalTime

/** Sample agenda for the demo. Titles, times and rooms are made up; there are no speakers on purpose. */
object TalkCatalog {
    val talks: List<Talk> = listOf(
        Talk("t01", "Opening keynote", "general", LocalTime.of(9, 30), 30, "Main hall"),
        Talk("t02", "Compose performance in practice", "android", LocalTime.of(10, 15), 40, "Room A"),
        Talk("t03", "Kotlin coroutines beyond launch", "android", LocalTime.of(10, 15), 40, "Room B"),
        Talk("t04", "Web components without the pain", "web", LocalTime.of(11, 0), 40, "Room C"),
        Talk("t05", "Shipping Gemini-powered features", "ai", LocalTime.of(11, 45), 40, "Room A"),
        Talk("t06", "Kubernetes cost control", "cloud", LocalTime.of(12, 30), 40, "Room B"),
        Talk("t07", "Lunch", "general", LocalTime.of(13, 30), 90, "Canteen"),
        Talk("t08", "Android on-device agents", "android", LocalTime.of(15, 0), 40, "Room A"),
        Talk("t09", "Offline-first with Room", "android", LocalTime.of(15, 45), 40, "Room B"),
        Talk("t10", "Edge functions at scale", "cloud", LocalTime.of(16, 30), 40, "Room C"),
        Talk("t11", "Prompting is not engineering", "ai", LocalTime.of(17, 15), 40, "Room A"),
        Talk("t12", "Closing and raffle", "general", LocalTime.of(18, 0), 30, "Main hall"),
    )

    val tracks: List<String> = talks.map { it.track }.distinct().sorted()
}
