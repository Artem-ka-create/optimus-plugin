package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.isHtmlTag
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

class ScopeRule : AccessibilityRule {
    override val id = "ScopeRule"
    override val displayName = "Scope could be only in <th> element"

    companion object {
        const val MESSAGE = "Accessibility: The 'scope' attribute is only valid on <th> elements"
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        // <th> is the only element where 'scope' is valid — nothing to report there.
        if (element.isHtmlTag("th")) return

        val scopeAttribute = element.attributes.firstOrNull {
            AttributeResolver.normalizeAttrName(it.name) == "scope"
        } ?: return

        holder.registerProblem(scopeAttribute, MESSAGE, ScopeRuleQuickFix())
    }
}

private class ScopeRuleQuickFix: LocalQuickFix{
    override fun getName(): String = "Remove scope attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val attr = descriptor.psiElement
        if (attr is XmlAttribute && attr.isValid) {
            attr.delete()
        }
    }
}
