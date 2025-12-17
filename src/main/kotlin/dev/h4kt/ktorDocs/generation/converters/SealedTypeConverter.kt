package dev.h4kt.ktorDocs.generation.converters

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

@OptIn(ExperimentalSerializationApi::class)
class SealedTypeConverter : TypeConverter() {

    override val priority = 1000

    override fun doesSupport(type: KType): Boolean {
        return type.classifierKClass.isSealed
    }

    override fun convert(
        type: KType,
        parentTypes: Collection<KType>,
        classifier: KClass<*>,
        convertDownstream: (type: KType) -> OpenApiSchema
    ): OpenApiSchema {
        val variants = classifier.sealedSubclasses.map {
            return@map convertDownstream(it.createType()) // TODO: type arguments
        }

        return OpenApiSchema.OneOf(
            variants = variants,
            nullable = type.isMarkedNullable
        )
    }

}
