package dev.h4kt.ktorDocs.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DocsName(
    val value: String
)
