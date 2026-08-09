package com.github.artemkacreate.optimusplugin.inspections.fixes

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlTag

class RemoveElementQuickFix(
    private val description: String = "Remove element"
): AccessibilityQuickFix {
    override fun getName(): String = description
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement
        if (tag is XmlTag && tag.isValid) {
            try {
                tag.delete()
            } catch (_: UnsupportedOperationException) {
                // JSX/TSX PSI does not support direct tree mutation
            }
        }
    }
}
