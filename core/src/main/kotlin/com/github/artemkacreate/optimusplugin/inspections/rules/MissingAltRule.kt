package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.fixes.AddAttributeQuickFix
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.isHtmlTag
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: <img> without alt attribute
 */
class MissingAltRule : AccessibilityRule {

    override val id = "missingAlt"
    override val displayName = "Missing alt"

    companion object {
        private val ALT_ATTRIBUTES = setOf("alt", ":alt", "v-bind:alt", "[alt]")
        private const val MESSAGE = "Accessibility: <img> tag without alt attribute"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.isHtmlTag("img")) return

        val hasAlt = element.attributes.any { it.name.lowercase() in ALT_ATTRIBUTES }
        if (!hasAlt) {
            holder.registerProblem(element, MESSAGE, AddAttributeQuickFix("alt", ""))
        }
    }
}
