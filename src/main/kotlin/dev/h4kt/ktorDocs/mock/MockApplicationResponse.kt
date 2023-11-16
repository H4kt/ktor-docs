package dev.h4kt.ktorDocs.mock

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

class MockApplicationResponse(
    override val call: ApplicationCall,
    cookies: ResponseCookies? = null,
    override val headers: ResponseHeaders = MockResponseHeaders(),
    override val isCommitted: Boolean = false,
    override val isSent: Boolean = false,
    override val pipeline: ApplicationSendPipeline = ApplicationSendPipeline(false)
) : ApplicationResponse {

    override val cookies = cookies ?: ResponseCookies(this, false)

    private var status: HttpStatusCode? = null

    @UseHttp2Push
    override fun push(builder: ResponsePushBuilder) {}

    override fun status(): HttpStatusCode? {
        return status
    }

    override fun status(value: HttpStatusCode) {
        status = value
    }

}
