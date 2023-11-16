package dev.h4kt.ktorDocs.mock

import io.ktor.http.*

class MockRequestConnectionPoint(
    @Deprecated("Use localHost or serverHost instead")
    override val host: String = "127.0.0.1",
    override val localAddress: String = "127.0.0.1",
    override val localHost: String = "127.0.0.1",
    override val localPort: Int = 1,
    override val method: HttpMethod = HttpMethod.Get,
    @Deprecated("Use localPort or serverPort instead")
    override val port: Int = 1,
    override val remoteAddress: String = "127.0.0.1",
    override val remoteHost: String = "127.0.0.1",
    override val remotePort: Int = 2,
    override val scheme: String = "http",
    override val serverHost: String = "127.0.0.1",
    override val serverPort: Int = 2,
    override val uri: String = "",
    override val version: String = "HTTP/2"
) : RequestConnectionPoint
