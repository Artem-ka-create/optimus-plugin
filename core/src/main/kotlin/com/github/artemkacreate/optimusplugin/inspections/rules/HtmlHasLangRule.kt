package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.fixes.AddAttributeQuickFix
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.isHtmlTag
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: <html> must have a lang attribute for accessibility.
 * Checks for title (static), :title (Vue shorthand), v-bind:title (Vue), and \[title\] (Angular).
 * Ensures the title is not empty/blank.
 */
class HtmlHasLangRule : AccessibilityRule {
    override val id = "htmlHasLangRule"
    override val displayName = "Html has lang"
    override val description = "The html element must have a lang attribute to help assistive technologies determine the language."

    companion object {
        private val LANG_ATTRIBUTES = setOf("lang", ":lang", "v-bind:lang", "[lang]")
        private const val MESSAGE = "Accessibility: <html> must have a lang attribute"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.isHtmlTag("html")) return

        val langAttr = element.attributes.find { it.name.lowercase() in LANG_ATTRIBUTES }

        val hasValidLang = when {
            langAttr == null -> false
            langAttr.name.lowercase() != "lang" -> true
            else -> !langAttr.value.isNullOrBlank()
        }

        if (!hasValidLang) {
            holder.registerProblem(element, MESSAGE, AddAttributeQuickFix("lang", "en"))
        }
    }
}
