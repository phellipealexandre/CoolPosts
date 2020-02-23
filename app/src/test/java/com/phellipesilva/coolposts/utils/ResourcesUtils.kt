package com.phellipesilva.coolposts.utils

object ResourcesUtils {

    fun readJsonFromResources(filePath: String): String {
        return this.javaClass
            .classLoader
            ?.getResourceAsStream(filePath)
            ?.bufferedReader().use { it!!.readText() }
    }
}