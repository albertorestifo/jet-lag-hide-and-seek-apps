package dev.restifo.hide_and_seek

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform