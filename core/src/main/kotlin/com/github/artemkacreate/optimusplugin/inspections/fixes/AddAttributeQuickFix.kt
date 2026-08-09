package com.github.artemkacreate.optimusplugin.inspections.fixes

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlTag

class AddAttributeQuickFix(
    private val attributeName: String,
    private val attributeValue: String
) : AccessibilityQuickFix {
    override fun getName(): String = "Add $attributeName=\"\" attribute"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            try {
                element.setAttribute(attributeName, attributeValue)
            } catch (_: UnsupportedOperationException) {
                // JSX/TSX PSI does not support direct tree mutation
            }
        }
    }
}
