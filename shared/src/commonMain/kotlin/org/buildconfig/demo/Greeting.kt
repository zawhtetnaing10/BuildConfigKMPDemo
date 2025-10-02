package org.buildconfig.demo

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}! This is the network url ${BuildConfigKMPDemo.apiEndPoint}"
    }
}