package dev.h4kt.ktorDocs.mock

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*

class MockApplicationCall(
    override val application: Application,
    override val attributes: Attributes = Attributes(),
    override val parameters: Parameters = Parameters.Empty,
    request: ApplicationRequest? = null,
    response: ApplicationResponse? = null
) : ApplicationCall {
    override val request = request ?: MockApplicationRequest(this)
    override val response = response ?: MockApplicationResponse(this)
}
