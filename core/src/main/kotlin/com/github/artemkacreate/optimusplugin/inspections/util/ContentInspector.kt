package com.github.artemkacreate.optimusplugin.inspections.util

import com.github.artemkacreate.optimusplugin.inspections.util.constants.AriaConstants
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlText

object ContentInspector {

    fun hasTextContent(element: XmlTag): Boolean {
        return element.value.children.isNotEmpty() || element.subTags.isNotEmpty() || element.value.trimmedText.isNotBlank()
    }

    fun XmlTag.containsXmlTextNonRecursive(): Boolean {
        return SyntaxTraverser.psiTraverser(this).filter(XmlText::class.java).firstOrNull() != null
    }

    /**
     * Collects and concatenates the visible text of all nested XmlText elements,
     * normalized to a single space-separated, trimmed string.
     * E.g. `<a>Click <span>here</span></a>` → "Click here"
     */
    fun XmlTag.collectNestedText(): String {
        return SyntaxTraverser.psiTraverser(this).filter(XmlText::class.java)
            .mapNotNull { it.value.trim().takeIf(String::isNotEmpty) }.joinToString(" ").trim()
    }

    /**
     * Checks if element has aria-label or aria-labelledby (in any binding form).
     */
    fun hasAriaLabel(element: XmlTag): Boolean {
        return element.attributes.any { it.name.lowercase() in AriaConstants.ARIA_LABEL_ATTRIBUTES }
    }
}
