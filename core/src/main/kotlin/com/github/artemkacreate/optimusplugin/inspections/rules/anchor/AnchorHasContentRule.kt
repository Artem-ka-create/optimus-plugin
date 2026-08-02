package com.github.artemkacreate.optimusplugin.inspections.rules.anchor

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.fixes.AddAttributeQuickFix
import com.github.artemkacreate.optimusplugin.inspections.util.ContentInspector.hasAriaLabel
import com.github.artemkacreate.optimusplugin.inspections.util.ContentInspector.hasTextContent
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.isHtmlTag
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: <a> must have text content or aria-label
 */
class AnchorHasContentRule : AccessibilityRule {

    override val id = "anchorHasContent"
    override val displayName = "Must have text content (or aria-label)"

    companion object {
        private const val MESSAGE = "Accessibility: <a> must have accessible text content"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.isHtmlTag("a")) return

        if (!hasAriaLabel(element) && !hasTextContent(element)) {
            holder.registerProblem(element, MESSAGE, AddAttributeQuickFix("aria-label", "anchor-content"))
        }
    }
}
