package com.github.artemkacreate.optimusplugin.inspections.fixes

import com.github.artemkacreate.optimusplugin.inspections.accessibility.AccessibilityQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

class RenameTagQuickFix(
    private val tag: String,
    private val newTagName: String,
) : AccessibilityQuickFix {
    override fun getName(): String = "Change element to <$tag> and remove $newTagName"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val attr = descriptor.psiElement as? XmlAttribute ?: return
        val tagEl = attr.parent
        if (tagEl !is XmlTag || !tagEl.isValid) return
        attr.delete()
        tagEl.name = tag
    }
}
