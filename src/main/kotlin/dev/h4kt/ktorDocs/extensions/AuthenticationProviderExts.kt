package dev.h4kt.ktorDocs.extensions

import io.ktor.server.auth.AuthenticationProvider

internal val AuthenticationProvider.nameOrDefault: String
    get() = name ?: "default"
