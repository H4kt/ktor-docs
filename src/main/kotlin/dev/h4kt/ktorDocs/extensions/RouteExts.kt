package dev.h4kt.ktorDocs.extensions

import dev.h4kt.ktorDocs.types.DocumentedRoute
import io.ktor.server.routing.*
import io.ktor.util.*

private val documentationAttributeKey = AttributeKey<DocumentedRoute>("documentation")

internal val Route.isDocumented: Boolean
    get() = attributes.contains(documentationAttributeKey)

internal var Route.documentation: DocumentedRoute
    get() = attributes[documentationAttributeKey]
    set(value) {
        attributes.put(documentationAttributeKey, value)
    }
