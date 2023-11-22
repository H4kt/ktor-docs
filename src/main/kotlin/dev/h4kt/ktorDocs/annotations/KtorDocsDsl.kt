package dev.h4kt.ktorDocs.annotations

@DslMarker
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class KtorDocsDsl
