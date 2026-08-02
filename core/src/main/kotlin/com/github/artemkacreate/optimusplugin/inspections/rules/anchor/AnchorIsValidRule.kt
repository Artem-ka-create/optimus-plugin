package com.github.artemkacreate.optimusplugin.inspections.rules.anchor

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.isHtmlTag
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: <a> must have a valid href attribute value.
 * Flags href="#", href="", and href="javascript:..." as invalid.
 * Supports static href and dynamic bindings (:href, v-bind:href, \[href\]) with literal string values.
 */
class AnchorIsValidRule : AccessibilityRule {

    override val id = "anchorIsValid"
    override val displayName = "Anchor must have valid href"

    companion object {
        private val HREF_ATTRIBUTES = setOf("href", ":href", "v-bind:href", "[href]")
        private const val MESSAGE = "Accessibility: <a> must have a valid href (not '#', empty, or 'javascript:')"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.isHtmlTag("a")) return

        // Find any href attribute (static or dynamic binding)
        val hrefAttr = element.attributes.find { it.name.lowercase() in HREF_ATTRIBUTES } ?: return

        // Resolve effective value (handles dynamic bindings automatically)
        val hrefValue = AttributeResolver.resolveAttributeValue(hrefAttr)

        // If dynamic and we can't extract a literal — skip (complex expression)
        if (AttributeResolver.isDynamicBinding(hrefAttr.name) && hrefValue == null) return

        // Validate the href value
        if (hrefValue.isNullOrBlank() || hrefValue == "#" || hrefValue.lowercase().startsWith("javascript:")) {
            holder.registerProblem(element, MESSAGE, ReplaceHrefQuickFix())
        }
    }
}

/**
 * QuickFix: replaces invalid href with "/" placeholder
 */
private class ReplaceHrefQuickFix : LocalQuickFix {
    override fun getName(): String = "Replace invalid href with \"/\""
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("href", "/")
        }
    }
}
