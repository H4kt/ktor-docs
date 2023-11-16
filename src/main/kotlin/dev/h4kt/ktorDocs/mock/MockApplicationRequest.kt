package dev.h4kt.ktorDocs.mock

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.utils.io.*

class MockApplicationRequest(
    override val call: ApplicationCall,
    cookies: RequestCookies? = null,
    override val headers: Headers = Headers.Empty,
    override val local: RequestConnectionPoint = MockRequestConnectionPoint(),
    override val pipeline: ApplicationReceivePipeline = ApplicationReceivePipeline(),
    override val queryParameters: Parameters = Parameters.Empty,
    override val rawQueryParameters: Parameters = Parameters.Empty,
) : ApplicationRequest {

    override val cookies: RequestCookies = cookies ?: RequestCookies(this)

    override fun receiveChannel(): ByteReadChannel {
        return ByteReadChannel(byteArrayOf())
    }

}
