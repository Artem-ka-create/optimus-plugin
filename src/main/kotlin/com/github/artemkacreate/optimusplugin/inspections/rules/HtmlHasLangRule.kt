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
 * Rule: <html> must have a lang attribute for accessibility.
 * Checks for title (static), :title (Vue shorthand), v-bind:title (Vue), and [title] (Angular).
 * Ensures the title is not empty/blank.
 */
class HtmlHasLangRule : AccessibilityRule {
    override val id = "htmlHasLangRule"
    override val displayName = "Html has lang"
    override val supportedExtensions = setOf(
        FileExtension.HTML,
        FileExtension.JS,
        FileExtension.JSX,
        FileExtension.TS,
        FileExtension.TSX,
        FileExtension.VUE
    )

    companion object {
        private val LANG_ATTRIBUTES = setOf("lang", ":lang", "v-bind:lang", "[lang]")
        private const val MESSAGE = "Accessibility: <html> must have a lang attribute"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.name.equals("html", true)) return

        val langAttr = element.attributes.find { it.name.lowercase() in LANG_ATTRIBUTES }

        val hasValidLang = when {
            langAttr == null -> false
            langAttr.name.lowercase() != "lang" -> true
            else -> !langAttr.value.isNullOrBlank()
        }

        if (!hasValidLang) {
            holder.registerProblem(element, MESSAGE, AddHtmlHasLangQuickFix())
        }
    }
}

/**
 * QuickFix: adds lang="en" attribute to <html> tag
 */
class AddHtmlHasLangQuickFix : LocalQuickFix {
    override fun getName(): String = "Add lang=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("lang", "en")
        }
    }
}
