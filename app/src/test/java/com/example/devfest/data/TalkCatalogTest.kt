package com.example.devfest.data

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Guards the real agenda (docs/AGENDA-SYNC.md) and the examples the talk shows live. */
class TalkCatalogTest {
    private val talks = TalkCatalog.talks

    @Test
    fun `ids are unique and the agenda is ordered by start time`() {
        assertEquals(talks.size, talks.map { it.id }.toSet().size)
        assertEquals(talks.sortedBy { it.start }.map { it.id }, talks.map { it.id })
    }

    @Test
    fun `only the documented tracks are used`() {
        assertEquals(setOf("ai", "android", "web", "demo", "general"), talks.map { it.track }.toSet())
    }

    @Test
    fun `the live demo examples keep working`() {
        val afternoonAi = talks.filter { it.track == "ai" && !it.start.isBefore(LocalTime.of(15, 0)) }
        assertEquals(listOf("t15", "t21"), afternoonAi.map { it.id })
        assertEquals("Pendiente de confirmar", talks.single { it.start == LocalTime.of(18, 15) }.title)
        assertTrue(talks.single { it.id == "t16" }.speaker.orEmpty().contains("Gema"))
    }

    @Test
    fun `breaks and the unconfirmed slot have no speaker`() {
        listOf("Welcome", "Breakfast", "Descanso", "Comida", "Mesa redonda", "Pendiente de confirmar")
            .forEach { title -> assertEquals(null, talks.single { it.title == title }.speaker) }
    }
}
