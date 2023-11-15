package dev.h4kt.ktorDocs.extensions

import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode

operator fun YamlMap.contains(key: String): Boolean {
    return get<YamlNode>(key) != null
}
