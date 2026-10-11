package com.example.devfest.domain

import java.time.LocalTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class UseCasesTest {
    private val talks = listOf(
        Talk("a", "Morning android", "android", LocalTime.of(10, 0), 40, "A"),
        Talk("b", "Afternoon android", "android", LocalTime.of(15, 0), 40, "A"),
        Talk("c", "Afternoon web", "web", LocalTime.of(15, 0), 40, "B"),
    )
    private val repository = FakeTalkRepository(talks)

    @Test
    fun `search filters by track and start time`() = runTest {
        val result = SearchTalks(repository)(track = "Android", after = LocalTime.of(14, 0))
        assertEquals(listOf("b"), result.map { it.id })
    }

    @Test
    fun `search treats a blank track as all tracks`() = runTest {
        assertEquals(3, SearchTalks(repository)(track = "  ").size)
    }

    @Test
    fun `adding favourites is idempotent`() = runTest {
        val add = AddToFavourites(repository)
        assertEquals(FavouritesChange(added = 1, total = 1), add(listOf("a")))
        assertEquals(FavouritesChange(added = 0, total = 1), add(listOf("a")))
    }

    @Test
    fun `adding an unknown id is rejected and changes nothing`() = runTest {
        val add = AddToFavourites(repository)
        assertThrows(IllegalArgumentException::class.java) { kotlinx.coroutines.runBlocking { add(listOf("a", "zzz")) } }
        assertEquals(emptySet<String>(), repository.favouriteIds())
    }

    @Test
    fun `clearing removes every favourite`() = runTest {
        AddToFavourites(repository)(listOf("a", "b"))
        ClearFavourites(repository)()
        assertEquals(emptySet<String>(), repository.favouriteIds())
    }
}
