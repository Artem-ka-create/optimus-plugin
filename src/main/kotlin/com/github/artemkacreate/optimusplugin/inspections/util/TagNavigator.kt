package com.github.artemkacreate.optimusplugin.inspections.util

import com.github.artemkacreate.optimusplugin.inspections.enums.TechnologyType
import com.github.artemkacreate.optimusplugin.inspections.util.TagClassifier.getFileTechnologyType
import com.github.artemkacreate.optimusplugin.inspections.util.constants.HtmlConstants
import com.intellij.psi.xml.XmlTag

object TagNavigator {

    fun XmlTag.isNativeHtmlElement(): Boolean {
        val raw = name
        if (raw.isEmpty() || '-' in raw ) return false

        val isComponentFramework = when (containingFile?.getFileTechnologyType()) {
            TechnologyType.REACT, TechnologyType.VUE, TechnologyType.ANGULAR -> true
            else -> false // VANILLA / plain HTML
        }
        if (isComponentFramework && raw != raw.lowercase()) return false
        return raw.lowercase() in HtmlConstants.NATIVE_HTML_TAGS
    }

    fun XmlTag.isHtmlTag(vararg tagNames: String): Boolean {
        if (!isNativeHtmlElement()) return false
        val normalized = name.lowercase()
        return tagNames.any { it == normalized }
    }

    fun XmlTag.nativeTagNameOrNull(): String? =
        if (isNativeHtmlElement()) name.lowercase() else null

    /**
     * Walks UP the tree and checks whether \[element\] is nested inside a tag
     * whose name matches \[ancestorTagName\] (case-insensitive).
     * E.g. an <input> nested inside a <label>.
     */
    fun isNestedInsideTag(element: XmlTag, ancestorTagName: String): Boolean {
        var parent = element.parentTag
        while (parent != null) {
            if (parent.name.equals(ancestorTagName, ignoreCase = true)) return true
            parent = parent.parentTag
        }
        return false
    }

    /**
     * Walks DOWN the tree and checks whether \[element\] contains any descendant tag
     * whose name (lowercased) is in \[tagNames\].
     * E.g. a <label> containing a nested <input>/<select>/<textarea>.
     */
    fun hasNestedTag(element: XmlTag, tagNames: Set<String>): Boolean {
        return generateSequence(element.subTags.asList()) { tags ->
            tags.flatMap { it.subTags.asList() }.takeIf { it.isNotEmpty() }
        }.flatten().any { it.name.lowercase() in tagNames }
    }


}
