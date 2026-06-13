package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
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
 * Supports static href and dynamic bindings (:href, v-bind:href, [href]) with literal string values.
 */
class AnchorIsValidRule : AccessibilityRule {

    override val id = "anchorIsValid"
    override val displayName = "Anchor must have valid href"
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX, FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        private val HREF_ATTRIBUTES = setOf("href", ":href", "v-bind:href", "[href]")
        private const val MESSAGE = "Accessibility: <a> must have a valid href (not '#', empty, or 'javascript:')"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.name.equals("a", ignoreCase = true)) return

        // Find any href attribute (static or dynamic binding)
        val hrefAttr = element.attributes.find { it.name.lowercase() in HREF_ATTRIBUTES } ?: return

        // Resolve effective value (handles dynamic bindings automatically)
        val hrefValue = ExtractionTool.resolveAttributeValue(hrefAttr)

        // If dynamic and we can't extract a literal — skip (complex expression)
        if (ExtractionTool.isDynamicBinding(hrefAttr.name) && hrefValue == null) return

        // Validate the href value
        if (hrefValue.isNullOrBlank() || hrefValue == "#" || hrefValue.lowercase().startsWith("javascript:")) {
            holder.registerProblem(element, MESSAGE, ReplaceHrefQuickFix())
        }
    }
}

/**
 * QuickFix: replaces invalid href with "/" placeholder
 */
class ReplaceHrefQuickFix : LocalQuickFix {
    override fun getName(): String = "Replace invalid href with \"/\""
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("href", "/")
        }
    }
}
