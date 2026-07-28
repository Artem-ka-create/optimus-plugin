package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.constants.AriaConstants
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.nativeTagNameOrNull
import com.github.artemkacreate.optimusplugin.inspections.util.constants.HtmlConstants
import com.github.artemkacreate.optimusplugin.inspections.util.constants.RoleTagConstants
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

class NoNonIntElementToIntRoleRule : AccessibilityRule {
    override val id: String = "noNonIntElementToIntRoleRule"
    override val displayName: String = "Non-interactive element must not have an interactive role"

    companion object {
        private const val MESSAGE =
            "Accessibility: non-interactive element has an interactive role. " +
                "Use a native interactive element (e.g. <button>) or remove the role."
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        val isNonInteractive = when (val tagName = element.nativeTagNameOrNull() ?: return) {
            "a" -> {
                val hasHref = element.getAttribute("href") != null || element.attributes.any {
                    val clean = ExtractionTool.normalizeAttrName(it.name)
                    clean == "routerlink" || clean == "to"
                }
                !hasHref
            }
            "audio", "video" -> element.getAttribute("controls") == null
            "form" -> false
            else -> tagName in HtmlConstants.NON_INTERACTIVE_TAGS
        }

        // Якщо елемент НЕ належить до суворо неінтерактивних — виходимо
        if (!isNonInteractive) return
        val roleAttribute =
            element.attributes.find { ExtractionTool.normalizeAttrName(it.name) == AriaConstants.ARIA_ROLE_ATTRIBUTE }
                ?: return

        // Normalize: role can be a space-separated token list; the first token wins.
        val roleValue = ExtractionTool.resolveAttributeValue(roleAttribute)
            ?.lowercase()?.trim()?.split(Regex("\\s+"))?.firstOrNull()
            ?: return

        if (roleValue in RoleTagConstants.ALL_INTERACTIVE_ROLES) {
            holder.registerProblem(
                element,
                MESSAGE,
                RemoveInteractiveRoleQuickFix(),
                ChangeNonInteractiveElementToDivQuickFix()
            )
        }
    }
}

/**
 * QuickFix: removes the interactive role so the element keeps its native
 * non-interactive semantics.
 */
private class RemoveInteractiveRoleQuickFix : LocalQuickFix {
    override fun getName(): String = "Remove interactive role"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement
        if (tag is XmlTag && tag.isValid) {
            tag.getAttribute(AriaConstants.ARIA_ROLE_ATTRIBUTE)?.delete()
        }
    }
}

/**
 * QuickFix: replaces the non-interactive element with a generic <div>, which may
 * legitimately carry an interactive role.
 */
private class ChangeNonInteractiveElementToDivQuickFix : LocalQuickFix {
    override fun getName(): String = "Change element to <div>"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement
        if (tag is XmlTag && tag.isValid) {
            tag.name = "div"
        }
    }
}

