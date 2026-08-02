package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.fixes.RemoveElementQuickFix
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.nativeTagNameOrNull
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

class NoDistractingElementsRule : AccessibilityRule {
    override val id = "noDistractingElements"
    override val displayName = "There should not be any distracting elements"

    companion object {
        private val DISTRACTING_ELEMENTS = setOf("marquee", "blink")
        private const val MESSAGE =
            "Accessibility: Do not use <marquee> or <blink> elements as they cause visually distracting motion and create accessibility barriers."
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        if (element.nativeTagNameOrNull() in DISTRACTING_ELEMENTS) {
            holder.registerProblem(element, MESSAGE, RemoveElementQuickFix("Remove distracting element"))
        }
    }
}
