package com.github.artemkacreate.optimusplugin.inspections.rules.aria

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.fixes.RemoveAttributeQuickFix
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.nativeTagNameOrNull
import com.github.artemkacreate.optimusplugin.inspections.util.constants.HtmlConstants
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: `aria-hidden="true"` must not be set on a focusable element.
 * A focusable element hidden from the accessibility tree becomes a confusing
 * "phantom" stop for screen reader users navigating by tab order.
 *
 * An element is considered focusable when it is one of the default focusable
 * elements (button, input, ...), has an anchor with an href, or has a
 * non-negative tabindex.
 */
class NoAriaHiddenOnFocusableRule : AccessibilityRule {

    override val id = "noAriaHiddenOnFocusable"
    override val displayName = "aria-hidden must not be set on a focusable element"

    companion object {
        private const val MESSAGE = "Accessibility: aria-hidden=\"true\" must not be set on a focusable element"
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        val ariaHiddenAttribute =
            element.attributes.find { AttributeResolver.normalizeAttrName(it.name) == "aria-hidden" } ?: return

        // Only flag when aria-hidden resolves to "true"
        val ariaHiddenValue = AttributeResolver.resolveAttributeValue(ariaHiddenAttribute)?.trim()
        if (ariaHiddenValue != "true") return

        if (isFocusable(element)) {
            holder.registerProblem(
                ariaHiddenAttribute, MESSAGE,
                RemoveAttributeQuickFix("Remove aria-hidden attribute")
            )
        }
    }

    private fun isFocusable(element: XmlTag): Boolean {
        val tagName = element.nativeTagNameOrNull()

        // Natively focusable elements
        if (tagName in HtmlConstants.DEFAULT_FOCUSABLE_ELEMENTS) return true

        // Anchors/areas are focusable only when they have an href
        if (tagName == "a" || tagName == "area") {
            val hasHref = element.attributes.any { AttributeResolver.normalizeAttrName(it.name) == "href" }
            if (hasHref) return true
        }

        // Any element with a non-negative tabindex is focusable
        val tabindexAttr = element.attributes.find { AttributeResolver.normalizeAttrName(it.name) == "tabindex" }
        if (tabindexAttr != null) {
            val tabindex =
                AttributeResolver.resolveAttributeValue(tabindexAttr)?.let { AttributeResolver.parseNumericValue(it) }
            if (tabindex != null && tabindex >= 0) return true
        }

        return false
    }
}

