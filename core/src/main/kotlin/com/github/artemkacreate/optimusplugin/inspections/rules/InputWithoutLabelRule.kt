package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.fixes.AddAttributeQuickFix
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.isHtmlTag
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: <input> without <label> or aria-label
 */
class InputWithoutLabelRule : AccessibilityRule {

    override val id = "inputWithoutLabel"
    override val displayName = "Input Without Label"
    override val description = "Input elements must have an associated label so users know what data to enter."

    companion object {
        private val LABEL_ATTRIBUTES = setOf("id", "aria-label", "aria-labelledby", "[aria-label]", ":aria-label")
        private const val MESSAGE = "Accessibility: <input> without associated label (add aria-label or id)"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.isHtmlTag("input")) return

        val typeValue = element.getAttributeValue("type")?.lowercase()
        if (typeValue == "hidden") return

        val hasLabel = element.attributes.any { it.name.lowercase() in LABEL_ATTRIBUTES }
        if (!hasLabel) {
            holder.registerProblem(element, MESSAGE, AddAttributeQuickFix("aria-label", "input-label-text"))
        }
    }
}
