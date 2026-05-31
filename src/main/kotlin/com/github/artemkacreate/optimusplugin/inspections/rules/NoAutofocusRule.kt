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
 * Rule: Elements should not use the autofocus attribute.
 * Autofocus can cause usability issues for keyboard and screen reader users
 * by unexpectedly moving focus when a page loads.
 * Applies to any HTML element, not just form controls.
 * Supports Vue (:autofocus, v-bind:autofocus) and Angular (\[autofocus], \[attr.autofocus]) bindings.
 */
class NoAutofocusRule : AccessibilityRule {

    override val id = "noAutofocus"
    override val displayName = "Element should not use autofocus"
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX,
        FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        // All forms of autofocus attribute across frameworks
        private val AUTOFOCUS_ATTRIBUTES = setOf(
            "autofocus",                          // HTML standard
            ":autofocus", "v-bind:autofocus",     // Vue
            "[autofocus]", "[attr.autofocus]"     // Angular
        )
        private const val MESSAGE = "Accessibility: Do not use 'autofocus' — it causes usability issues for screen readers"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return

        val hasAutofocus = element.attributes.any { it.name.lowercase() in AUTOFOCUS_ATTRIBUTES }
        if (hasAutofocus) {
            holder.registerProblem(element, MESSAGE, RemoveAutofocusQuickFix())
        }
    }
}

/**
 * QuickFix: removes the autofocus attribute from the element
 */
class RemoveAutofocusQuickFix : LocalQuickFix {
    override fun getName(): String = "Remove autofocus attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.attributes
                .filter { it.name.lowercase() in setOf("autofocus", ":autofocus", "v-bind:autofocus", "[autofocus]", "[attr.autofocus]") }
                .forEach { it.delete() }
        }
    }
}
