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
 * Rule: <img> without alt attribute
 */
class MissingAltRule : AccessibilityRule {

    override val id = "missingAlt"
    override val displayName = "Missing alt"
    override val supportedExtensions = setOf(
        FileExtension.HTML,
        FileExtension.JS,
        FileExtension.JSX,
        FileExtension.TS,
        FileExtension.TSX,
        FileExtension.VUE
    )

    companion object {
        private val ALT_ATTRIBUTES = setOf("alt", ":alt", "v-bind:alt", "[alt]")
        private const val MESSAGE = "Accessibility: <img> tag without alt attribute"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.name.equals("img", ignoreCase = true)) return

        val hasAlt = element.attributes.any { it.name.lowercase() in ALT_ATTRIBUTES }
        if (!hasAlt) {
            holder.registerProblem(element, MESSAGE, AddAltAttributeQuickFix())
        }
    }
}

/**
 * QuickFix: adds alt="" attribute to <img> tag
 */
class AddAltAttributeQuickFix : LocalQuickFix {
    override fun getName(): String = "Add alt=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("alt", "")
        }
    }
}
