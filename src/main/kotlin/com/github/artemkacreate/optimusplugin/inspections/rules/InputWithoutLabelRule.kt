package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: <input> without <label> or aria-label
 */
class InputWithoutLabelRule : AccessibilityRule {

    override val id = "inputWithoutLabel"
    override val displayName = "Input Without Label"
    override val supportedExtensions = setOf(
        FileExtension.HTML,
        FileExtension.JS,
        FileExtension.JSX,
        FileExtension.TS,
        FileExtension.TSX,
        FileExtension.VUE

    )

    companion object {
        private val LABEL_ATTRIBUTES = setOf("id", "aria-label", "aria-labelledby", "[aria-label]", ":aria-label")
        private const val MESSAGE = "Accessibility: <input> without associated label (add aria-label or id)"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.name.equals("input", ignoreCase = true)) return

        val typeValue = element.getAttributeValue("type")?.lowercase()
        if (typeValue == "hidden") return

        val hasLabel = element.attributes.any { it.name.lowercase() in LABEL_ATTRIBUTES }
        if (!hasLabel) {
            holder.registerProblem(element, MESSAGE, AddAriaLabelQuickFix())
        }
    }
}

/**
 * QuickFix: adds aria-label="" attribute to <input> tag
 */
class AddAriaLabelQuickFix : LocalQuickFix {
    override fun getName(): String = "Add aria-label=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("aria-label", "")
        }
    }
}
