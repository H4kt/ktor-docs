package dev.h4kt.ktorDocs.plugin

import dev.h4kt.ktorDocs.types.openapi.components.OpenApiSchema
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

class TypeMap : Iterable<Map.Entry<KType, OpenApiSchema.Reference>> {

    private val names = mutableSetOf<String>()
    private val referenceByType = mutableMapOf<KType, OpenApiSchema.Reference>()

    override fun iterator(): Iterator<Map.Entry<KType, OpenApiSchema.Reference>> {
        return referenceByType.toMap().iterator()
    }

    operator fun contains(key: KType): Boolean {
        return key in referenceByType
    }

    fun getTypeReference(type: KType): OpenApiSchema.Reference? {
        return referenceByType[type]
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getTypeReferenceOrRegister(type: KType): OpenApiSchema.Reference {

        val existing = referenceByType[type]
        if (existing != null) {
            return existing
        }

        return register(type)
    }

    fun register(type: KType): OpenApiSchema.Reference {

        val fqn = type.resolveFqn()
            .replace("<", "")
            .replace(">", "")
            .replace(", ", "")

        val fqnParts = fqn.split('.')

        fun List<String>.joinToCamelCaseString() = joinToString("") { part -> part.replaceFirstChar { it.uppercaseChar() } }

        var parts = 0
        var name: String
        do {
            ++parts
            name = fqnParts.takeLast(parts).joinToCamelCaseString()
        } while (name in names)

        val reference = OpenApiSchema.Reference("#/components/schemas/$name")

        names += name
        referenceByType[type] = reference

        return reference
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun KType.resolveFqn(): String {

        if (isList()) {
            return arguments.first().type!!.javaType.typeName
        }

        return javaType.typeName
    }

    private fun KType.isList(): Boolean {
        val listType = typeOf<List<*>>()
        return this == listType || isSubtypeOf(listType)
    }

}
