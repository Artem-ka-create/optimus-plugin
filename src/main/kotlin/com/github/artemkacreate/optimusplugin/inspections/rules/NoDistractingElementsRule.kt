package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.nativeTagNameOrNull
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
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
            holder.registerProblem(element, MESSAGE, RemoveDistractingElementQuickFix())
        }
    }
}

private class RemoveDistractingElementQuickFix : LocalQuickFix {

    override fun getName(): String = "Remove distracting element"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement
        if (tag is XmlTag && tag.isValid) {
            tag.delete()
        }
    }
}
