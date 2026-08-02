package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.fixes.AddAttributeQuickFix
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.isHtmlTag
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: <iframe> must have a title attribute for accessibility.
 * Checks for title (static), :title (Vue shorthand), v-bind:title (Vue), and \[title\] (Angular).
 * Ensures the title is not empty/blank.
 */
class IframeHasTitleRule : AccessibilityRule {
    override val id = "iframeHasTitle"
    override val displayName = "Iframe must have title"

    companion object {
        private val TITLE_ATTRIBUTES = setOf("title", ":title", "v-bind:title", "[title]")
        private const val MESSAGE = "Accessibility: <iframe> must have a title attribute"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.isHtmlTag("iframe")) return

        val titleAttr = element.attributes.find { it.name.lowercase() in TITLE_ATTRIBUTES }

        // If no title attribute at all, or static title is empty/blank — flag it
        val hasValidTitle = when {
            titleAttr == null -> false
            // Dynamic bindings (:title, [title]) — presence is enough (can't validate expression)
            titleAttr.name.lowercase() != "title" -> true
            // Static title — must not be empty
            else -> !titleAttr.value.isNullOrBlank()
        }

        if (!hasValidTitle) {
            holder.registerProblem(element, MESSAGE, AddAttributeQuickFix("title", "title-example"))
        }
    }
}
