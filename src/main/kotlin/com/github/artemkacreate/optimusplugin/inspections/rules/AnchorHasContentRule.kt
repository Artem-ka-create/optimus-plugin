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
 * Rule: <a> must have text content or aria-label
 */
class AnchorHasContentRule : AccessibilityRule {

    override val id = "anchorHasContent"
    override val displayName = "Must have text content (or aria-label)"
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX, FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        private const val MESSAGE = "Accessibility: <a> must have accessible text content"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.name.equals("a", ignoreCase = true)) return

        if (!ExtractionTool.hasAriaLabel(element) && !ExtractionTool.hasTextContent(element)) {
            holder.registerProblem(element, MESSAGE, AddAnchorHasContentAttributeQuickFix())
        }
    }
}

class AddAnchorHasContentAttributeQuickFix : LocalQuickFix {
    override fun getName(): String = "Add aria-label=\"\" attribute to <a> tag"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("aria-label", "")
        }
    }
}
