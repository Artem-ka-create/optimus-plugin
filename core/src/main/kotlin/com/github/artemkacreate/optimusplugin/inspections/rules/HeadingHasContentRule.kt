package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.fixes.AddAttributeQuickFix
import com.github.artemkacreate.optimusplugin.inspections.util.ContentInspector
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.nativeTagNameOrNull
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: Headings (<h1>-<h6>) must have accessible text content.
 * Checks for text content, child elements, aria-label, aria-labelledby,
 * and framework-specific dynamic content directives (Vue: v-text/v-html, Angular: \[innerText\]/\[innerHTML\]).
 */
class HeadingHasContentRule : AccessibilityRule {
    override val id = "headingHasContent"
    override val displayName = "Heading must have content"

    companion object {
        private val HEADING_TAGS = setOf("h1", "h2", "h3", "h4", "h5", "h6")
        // Vue/Angular directives that provide dynamic content
        private val DYNAMIC_CONTENT_ATTRIBUTES = setOf(
            "v-text", "v-html",           // Vue
            "[innertext]", "[innerhtml]",  // Angular
            "dangerouslysetinnerhtml"      // React (JSX)
        )
        private const val MESSAGE = "Accessibility: Heading must have text content or aria-label"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        val tagName = element.nativeTagNameOrNull() ?: return
        if (tagName !in HEADING_TAGS) return

        val hasAriaLabel = ContentInspector.hasAriaLabel(element)
        val hasContent = ContentInspector.hasTextContent(element)
        val hasDynamicContent = element.attributes.any { it.name.lowercase() in DYNAMIC_CONTENT_ATTRIBUTES }

        if (!hasAriaLabel && !hasContent && !hasDynamicContent) {
            holder.registerProblem(element, MESSAGE, AddAttributeQuickFix("aria-label", "heading-text"))
        }
    }
}
