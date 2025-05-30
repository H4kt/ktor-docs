package dev.h4kt.ktorDocs.mock

import io.ktor.http.Headers
import io.ktor.http.Parameters
import io.ktor.http.RequestConnectionPoint
import io.ktor.server.application.PipelineCall
import io.ktor.server.request.ApplicationReceivePipeline
import io.ktor.server.request.PipelineRequest
import io.ktor.server.request.RequestCookies
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI

class MockPipelineRequest(
    override val call: PipelineCall,
    cookies: RequestCookies? = null,
    override val headers: Headers = Headers.Empty,
    override val local: RequestConnectionPoint = MockRequestConnectionPoint(),
    override val queryParameters: Parameters = Parameters.Empty,
    override val rawQueryParameters: Parameters = Parameters.Empty,
    override val pipeline: ApplicationReceivePipeline = ApplicationReceivePipeline(),
) : PipelineRequest {

    override val cookies: RequestCookies = cookies ?: RequestCookies(this)

    override fun receiveChannel(): ByteReadChannel {
        return ByteReadChannel(byteArrayOf())
    }

    @InternalAPI
    override fun setHeader(
        name: String,
        values: List<String>?
    ) {
        // NO-OP
    }

    @InternalAPI
    override fun setReceiveChannel(channel: ByteReadChannel) {
        // NO-OP
    }

}
