package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.util.CommonValues
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.nativeTagNameOrNull
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
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
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX, FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        private const val MESSAGE =
            "Accessibility: aria-hidden=\"true\" must not be set on a focusable element"
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        val ariaHiddenAttribute = element.attributes
            .find { ExtractionTool.normalizeAttrName(it.name) == "aria-hidden" } ?: return

        // Only flag when aria-hidden resolves to "true"
        val ariaHiddenValue = ExtractionTool.resolveAttributeValue(ariaHiddenAttribute)?.trim()
        if (ariaHiddenValue != "true") return

        if (isFocusable(element)) {
            holder.registerProblem(ariaHiddenAttribute, MESSAGE, RemoveAriaHiddenQuickFix())
        }
    }

    private fun isFocusable(element: XmlTag): Boolean {
        val tagName = element.nativeTagNameOrNull()

        // Natively focusable elements
        if (tagName in CommonValues.DEFAULT_FOCUSABLE_ELEMENTS) return true

        // Anchors/areas are focusable only when they have an href
        if (tagName == "a" || tagName == "area") {
            val hasHref = element.attributes.any { ExtractionTool.normalizeAttrName(it.name) == "href" }
            if (hasHref) return true
        }

        // Any element with a non-negative tabindex is focusable
        val tabindexAttr = element.attributes
            .find { ExtractionTool.normalizeAttrName(it.name) == "tabindex" }
        if (tabindexAttr != null) {
            val tabindex = ExtractionTool.resolveAttributeValue(tabindexAttr)
                ?.let { ExtractionTool.parseNumericValue(it) }
            if (tabindex != null && tabindex >= 0) return true
        }

        return false
    }
}

/**
 * QuickFix: removes the aria-hidden attribute from the focusable element.
 */
private class RemoveAriaHiddenQuickFix : LocalQuickFix {

    override fun getName(): String = "Remove aria-hidden attribute"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val attr = descriptor.psiElement
        if (attr is XmlAttribute && attr.isValid) {
            attr.delete()
        }
    }
}
