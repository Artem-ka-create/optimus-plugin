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
 * Rule: Elements should not use the accessKey attribute.
 * Applies to any HTML element, not just form controls.
 * Supports Vue (:accessKey, v-bind:accessKey) and Angular ([accessKey], [attr.accessKey]) bindings.
 */
class NoAccessKeyRule : AccessibilityRule {

    override val id = "noAccessKey"
    override val displayName = "Element should not use accessKey"
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX,
        FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        private val ACCESSKEY_ATTRIBUTES = setOf(
            "accesskey",                          // HTML standard
            ":accesskey", "v-bind:accesskey",     // Vue
            "[accesskey]", "[attr.accesskey]"     // Angular
        )
        private const val MESSAGE = "Accessibility: Do not use 'accessKey' — it causes usability issues for screen readers"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return

        if (element.attributes.any { it.name.lowercase() in ACCESSKEY_ATTRIBUTES }) {
            holder.registerProblem(element, MESSAGE, RemoveAccessKeyQuickFix())
        }
    }
}

/**
 * QuickFix: removes the accessKey attribute from the element
 */
class RemoveAccessKeyQuickFix : LocalQuickFix {
    override fun getName(): String = "Remove accessKey attribute"
    override fun getFamilyName(): String = "Accessibility fixes"

    companion object {
        private val ACCESSKEY_ATTRIBUTES = setOf(
            "accesskey", ":accesskey", "v-bind:accesskey",
            "[accesskey]", "[attr.accesskey]"
        )
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.attributes
                .filter { it.name.lowercase() in ACCESSKEY_ATTRIBUTES }
                .forEach { it.delete() }
        }
    }
}
