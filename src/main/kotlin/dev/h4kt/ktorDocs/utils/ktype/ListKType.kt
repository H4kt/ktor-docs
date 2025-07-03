package dev.h4kt.ktorDocs.utils.ktype

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.typeOf

internal val KType.isList: Boolean
    get() = this.classifier == typeOf<List<*>>().classifier ||
            (this.classifier as? KClass<*>)?.isSubclassOf((typeOf<List<*>>().classifier as KClass<*>)) == true
