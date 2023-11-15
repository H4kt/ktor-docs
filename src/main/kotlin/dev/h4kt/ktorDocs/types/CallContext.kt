package dev.h4kt.ktorDocs.types

import io.ktor.server.application.*

data class CallContext<TParams>(
    val call: ApplicationCall,
    val parameters: TParams
)
