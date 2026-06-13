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
 * Rule: Headings (<h1>-<h6>) must have accessible text content.
 * Checks for text content, child elements, aria-label, aria-labelledby,
 * and framework-specific dynamic content directives (Vue: v-text/v-html, Angular: [innerText]/[innerHTML]).
 */
class HeadingHasContentRule : AccessibilityRule {
    override val id = "headingHasContent"
    override val displayName = "Heading must have content"
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX,
        FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

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
        if (element.name.lowercase() !in HEADING_TAGS) return

        val hasAriaLabel = ExtractionTool.hasAriaLabel(element)
        val hasContent = ExtractionTool.hasTextContent(element)
        val hasDynamicContent = element.attributes.any { it.name.lowercase() in DYNAMIC_CONTENT_ATTRIBUTES }

        if (!hasAriaLabel && !hasContent && !hasDynamicContent) {
            holder.registerProblem(element, MESSAGE, AddHeadingAriaLabelQuickFix())
        }
    }
}

/**
 * QuickFix: adds aria-label="" attribute to heading tag
 */
class AddHeadingAriaLabelQuickFix : LocalQuickFix {
    override fun getName(): String = "Add aria-label=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("aria-label", "")
        }
    }
}
