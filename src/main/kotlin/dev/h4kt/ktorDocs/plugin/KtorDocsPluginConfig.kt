package dev.h4kt.ktorDocs.plugin

import dev.h4kt.ktorDocs.generation.converters.auth.AuthProviderConverter
import dev.h4kt.ktorDocs.generation.converters.auth.BasicAuthProviderConverter
import dev.h4kt.ktorDocs.generation.converters.auth.BearerAuthProviderConverter
import dev.h4kt.ktorDocs.generation.converters.auth.OAuthAuthProviderConverter
import dev.h4kt.ktorDocs.generation.converters.type.CollectionTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.DataClassTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.DataObjectTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.EnumTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.JavaUuidTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.KotlinTimeTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.KotlinUuidTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.KotlinXDateTimeTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.PrimitiveTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.SealedTypeConverter
import dev.h4kt.ktorDocs.generation.converters.type.TypeConverter
import dev.h4kt.ktorDocs.types.openapi.OpenApiServer
import dev.h4kt.ktorDocs.types.openapi.OpenApiSpec
import io.ktor.server.plugins.swagger.SwaggerConfig

@Suppress("UNUSED")
class KtorDocsPluginConfig {

    class Swagger {

        var isEnabled = true
        var path = "swagger"

        internal var config: SwaggerConfig.() -> Unit = {}

        fun config(configure: SwaggerConfig.() -> Unit) {
            config = configure
        }

    }

    class OpenApi {

        val info = OpenApiSpec.Info(
            title = "Default title",
            version = "v1",
            description = ""
        )

        val servers = mutableListOf<OpenApiServer>()

        fun info(configure: OpenApiSpec.Info.() -> Unit) {
            info.apply(configure)
        }

        fun server(configure: OpenApiServer.() -> Unit) {
            servers += OpenApiServer("").apply(configure)
        }

    }

    val swagger = Swagger()
    val openApi = OpenApi()

    var documentationFilePath: String = "documentation.yml"

    internal val typeConverters = mutableListOf(
        CollectionTypeConverter(),
        DataClassTypeConverter(),
        DataObjectTypeConverter(),
        EnumTypeConverter(),
        JavaUuidTypeConverter(),
        KotlinTimeTypeConverter(),
        KotlinUuidTypeConverter(),
        KotlinXDateTimeTypeConverter(),
        PrimitiveTypeConverter(),
        SealedTypeConverter()
    )

    internal val authProviderConverters = mutableListOf(
        BasicAuthProviderConverter(),
        BearerAuthProviderConverter(),
        OAuthAuthProviderConverter()
    )

    fun swagger(configure: Swagger.() -> Unit) {
        swagger.apply(configure)
    }

    fun openApi(configure: OpenApi.() -> Unit) {
        openApi.apply(configure)
    }

    fun typeConverters(vararg converters: TypeConverter) {
        this.typeConverters.addAll(converters)
    }

    fun authProviderConverters(vararg converters: AuthProviderConverter) {
        this.authProviderConverters.addAll(converters)
    }

}
