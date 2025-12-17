package dev.h4kt.ktorDocs.exceptions

import kotlin.reflect.KClass

class ClashingNamesException(
    val clashes: Map<String, List<KClass<*>>>
) : RuntimeException(buildMessage(clashes))

private fun buildMessage(
    clashes: Map<String, List<KClass<*>>>
): String {
    return buildString {
        append("Detected clashing names in documentation:\n")

        clashes.forEach { (string, classes) ->
            append("   $string -> ${classes.joinToString { it.qualifiedName ?: "Unknown" }}\n")
        }
    }
}
