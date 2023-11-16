package dev.h4kt.ktorDocs.mock

import io.ktor.server.response.*

class MockResponseHeaders : ResponseHeaders() {

    private val engineHeaders = mutableMapOf<String, MutableList<String>>()

    override fun engineAppendHeader(
        name: String,
        value: String
    ) {
        engineHeaders.computeIfAbsent(name) { mutableListOf() }
            .add(value)
    }

    override fun getEngineHeaderNames(): List<String> {
        return engineHeaders.keys.toList()
    }

    override fun getEngineHeaderValues(name: String): List<String> {
        return engineHeaders[name] ?: emptyList()
    }

}
