package org.buildconfig.demo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform