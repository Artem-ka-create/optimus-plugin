package com.github.artemkacreate.optimusplugin.inspections.fixes

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlAttribute

class RemoveAttributeQuickFix(
    private val description: String = "Remove attribute"
): AccessibilityQuickFix {

    override fun getName(): String = description
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val attr = descriptor.psiElement
        if (attr is XmlAttribute && attr.isValid) {
            try {
                attr.delete()
            } catch (_: UnsupportedOperationException) {
                // JSX/TSX PSI (JSXmlAttributeImpl) does not support direct tree mutation
            }
        }
    }
}
