package dev.h4kt.ktorDocs.mock

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.PipelineCall
import io.ktor.server.response.ApplicationSendPipeline
import io.ktor.server.response.PipelineResponse
import io.ktor.server.response.ResponseCookies
import io.ktor.server.response.ResponseHeaders
import io.ktor.server.response.ResponsePushBuilder
import io.ktor.server.response.UseHttp2Push

class MockPipelineResponse(
    override val call: PipelineCall,
    cookies: ResponseCookies? = null,
    override val headers: ResponseHeaders = MockResponseHeaders(),
    override val isCommitted: Boolean = false,
    override val isSent: Boolean = false,
    override val pipeline: ApplicationSendPipeline = ApplicationSendPipeline()
) : PipelineResponse {

    override val cookies = cookies ?: ResponseCookies(this)

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
