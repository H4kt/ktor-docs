package dev.h4kt.ktorDocs.mock

import io.ktor.http.Parameters
import io.ktor.server.application.Application
import io.ktor.server.application.PipelineCall
import io.ktor.server.request.PipelineRequest
import io.ktor.server.response.PipelineResponse
import io.ktor.util.Attributes
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.CoroutineName

class MockPipelineCall(
    override val application: Application,
    override val attributes: Attributes = Attributes(),
    override val parameters: Parameters = Parameters.Empty,
    request: PipelineRequest? = null,
    response: PipelineResponse? = null
) : PipelineCall {

    override val request = request ?: MockPipelineRequest(this)
    override val response = response ?: MockPipelineResponse(this)
    override val coroutineContext = CoroutineName("MockPipelineCall")

    override suspend fun <T> receiveNullable(
        typeInfo: TypeInfo
    ): T? {
        // NO-OP
        return null
    }

    override suspend fun respond(
        message: Any?,
        typeInfo: TypeInfo?
    ) {
        // NO-OP
    }

}
