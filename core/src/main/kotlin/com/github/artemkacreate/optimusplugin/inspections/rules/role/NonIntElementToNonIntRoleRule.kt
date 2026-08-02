package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.accessibility.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.nativeTagNameOrNull
import com.github.artemkacreate.optimusplugin.inspections.util.constants.AriaConstants
import com.github.artemkacreate.optimusplugin.inspections.util.constants.HtmlConstants
import com.github.artemkacreate.optimusplugin.inspections.util.constants.RoleTagConstants
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

class NonIntElementToNonIntRoleRule : AccessibilityRule {
    override val id: String = "nonIntElementToNonIntRoleRule"
    override val displayName: String = "Interactive element must not have a non-interactive role"

    companion object {
        private const val MESSAGE =
            "Accessibility: interactive element has a non-interactive role. " +
                "Use a non-interactive element (e.g. <div>) or an interactive role instead."

        /** A common interactive role used by the quick fix. */
        private const val DEFAULT_INTERACTIVE_ROLE = "button"
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        val isInteractive = when (val tagName = element.nativeTagNameOrNull() ?: return) {
            "a" -> element.getAttribute("href") != null || element.attributes.any {
                val clean = AttributeResolver.normalizeAttrName(it.name)
                clean == "routerlink" || clean == "to"
            }

            "audio", "video" -> element.getAttribute("controls") != null
            "input" -> element.getAttributeValue("type")?.lowercase()?.trim() != "hidden"
            else -> tagName in HtmlConstants.ALL_INTERACTIVE_TAGS
        }

        if (!isInteractive) return

        val roleAttribute =
            element.attributes.find { AttributeResolver.normalizeAttrName(it.name) == AriaConstants.ARIA_ROLE_ATTRIBUTE }
                ?: return

        // Normalize: role can be a space-separated token list; the first token wins.
        val roleValue = AttributeResolver.resolveAttributeValue(roleAttribute)
            ?.lowercase()?.trim()?.split(Regex("\\s+"))?.firstOrNull()
            ?: return

        if (roleValue in RoleTagConstants.ALL_NON_INTERACTIVE_ROLES) {
            holder.registerProblem(
                element,
                MESSAGE,
                ChangeElementToDivQuickFix(),
                ChangeRoleToInteractiveQuickFix(DEFAULT_INTERACTIVE_ROLE)
            )
        }
    }
}

/**
 * QuickFix: replaces the interactive element with a <div> so a non-interactive
 * role becomes valid on it.
 */
private class ChangeElementToDivQuickFix : LocalQuickFix {
    override fun getName(): String = "Change element to <div>"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement
        if (tag is XmlTag && tag.isValid) {
            tag.name = "div"
        }
    }
}

/**
 * QuickFix: changes the role to a common interactive role, keeping the element
 * interactive and consistent with its behaviour.
 */
private class ChangeRoleToInteractiveQuickFix(private val role: String) : LocalQuickFix {
    override fun getName(): String = "Change role to \"$role\" (interactive)"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement
        if (tag is XmlTag && tag.isValid) {
            tag.setAttribute(AriaConstants.ARIA_ROLE_ATTRIBUTE, role)
        }
    }
}

