package com.example.devfest.data

import com.example.devfest.domain.Talk
import java.time.LocalTime

/**
 * The DevFest Ourense 2026 agenda (7 Nov 2026, ESEI Ourense), copied from https://gdgourense.es/events/df26,
 * read 2026-11-02. See docs/AGENDA-SYNC.md.
 *
 * Titles, speakers, rooms and start times are the organisers'. The `track` is **our own classification** of each
 * title (the official site only has four themes and does not tag sessions): ai, android, web, demo, general.
 * The web publishes no end times, so none are stored. `speaker` is null for breaks and the unconfirmed slot.
 */
object TalkCatalog {
    val talks: List<Talk> = listOf(
        Talk("t01", "Welcome", null, "general", LocalTime.of(9, 0), "Cinnamon Bun"),
        Talk("t02", "Breakfast", null, "general", LocalTime.of(9, 15), "Cinnamon Bun"),
        Talk("t03", "Presentación", null, "general", LocalTime.of(10, 0), "Red Velvet"),
        Talk(
            "t04", "Orchestrating Autonomy: Practical Frameworks for Agentic Software Engineering",
            "Alejandro de la Cruz López", "ai", LocalTime.of(10, 15), "Red Velvet",
        ),
        Talk(
            "t05", "From Tokens to Circuits: An Introduction to Transformers and Mechanistic Interpretability",
            "Rubén Fernández Boullón", "ai", LocalTime.of(10, 30), "Honeycomb",
        ),
        Talk("t06", "Sizit", null, "demo", LocalTime.of(11, 15), "Red Velvet"),
        Talk("t07", "Revive tus fotos con IA", "Martín Díaz Fernández", "ai", LocalTime.of(11, 30), "Red Velvet"),
        Talk(
            "t08", "NotebookLM: Turning Notes into Knowledge with Google’s AI-Powered Research Assistant",
            "Roushanak R", "ai", LocalTime.of(11, 30), "Honeycomb",
        ),
        Talk("t09", "Descanso", null, "general", LocalTime.of(12, 30), "Cinnamon Bun"),
        Talk(
            "t10", "Convierte tu app en una skill con App Functions",
            "Juanje Cilla", "android", LocalTime.of(13, 0), "Red Velvet",
        ),
        Talk("t11", "Trenio", null, "demo", LocalTime.of(14, 0), "Red Velvet"),
        Talk(
            "t12", "Cómo fracasar estrepitosamente al explicar tus proyectos",
            "Martín Díaz Fernández", "general", LocalTime.of(14, 15), "Red Velvet",
        ),
        Talk(
            "t13", "Primeros Auxilios Digitales",
            "Cesáreo González Rodríguez", "general", LocalTime.of(14, 45), "Red Velvet",
        ),
        Talk("t14", "Comida", null, "general", LocalTime.of(15, 0), "Cinnamon Bun"),
        Talk(
            "t15", "When Software Stops Being Deterministic: Engineering Practices for AI-Native Systems",
            "Susanna Wong", "ai", LocalTime.of(16, 0), "Red Velvet",
        ),
        Talk(
            "t16", "Set Sail for the Grand Line: Charting Android Apps with Agents and Android CLI",
            "Gema Socorro", "android", LocalTime.of(16, 15), "Honeycomb",
        ),
        Talk("t17", "Docucraft", null, "demo", LocalTime.of(17, 0), "Red Velvet"),
        Talk(
            "t18", "AI in the browser: run your model locally with WebLLM and Angular",
            "Davide Passafaro", "web", LocalTime.of(17, 30), "Red Velvet",
        ),
        Talk(
            "t19", "Prepara tu web para la era de la IA agéntica",
            "Sergi Olives", "web", LocalTime.of(17, 30), "Honeycomb",
        ),
        Talk("t20", "Pendiente de confirmar", null, "demo", LocalTime.of(18, 15), "Red Velvet"),
        Talk(
            "t21", "Enseñando cultura, costumbres e idioma a Gemma 4",
            "Eric Risco", "ai", LocalTime.of(18, 30), "Red Velvet",
        ),
        Talk("t22", "Mesa redonda", null, "general", LocalTime.of(19, 30), "Red Velvet"),
        Talk("t23", "Clausura", "Cesáreo González Rodríguez", "general", LocalTime.of(20, 30), "Red Velvet"),
    )

    val tracks: List<String> = talks.map { it.track }.distinct().sorted()
}
