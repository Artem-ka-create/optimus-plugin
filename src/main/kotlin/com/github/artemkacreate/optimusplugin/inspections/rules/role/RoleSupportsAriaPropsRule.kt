package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.constants.AriaConstants
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.normalizeAttrName
import com.github.artemkacreate.optimusplugin.inspections.util.constants.RoleTagConstants
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

class RoleSupportsAriaPropsRule : AccessibilityRule {

    override val id = "roleSupportsAriaProps"
    override val displayName = "Elements must only use ARIA attributes supported by their role"

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return

        val roleAttr = element.attributes.firstOrNull {
            normalizeAttrName(it.name) == AriaConstants.ARIA_ROLE_ATTRIBUTE
        } ?: return

        // Resolve the role value (handles Vue ':role', Angular '[attr.role]', JSX role={"x"}).
        val roleValue = (ExtractionTool.resolveAttributeValue(roleAttr)
            ?: roleAttr.value?.takeIf { it.isNotBlank() }
            ?: return)
            .trim()
            .lowercase()

        // Only check roles we actually recognize; unknown roles are handled elsewhere.
        if (roleValue !in AriaConstants.VALID_ARIA_ROLE_VALUES) return

        val supportedProps = RoleTagConstants.supportedAriaPropsForRole(roleValue)

        for (attr in element.attributes) {
            val normalized = normalizeAttrName(attr.name)

            // Only inspect aria-* attributes.
            if (!normalized.startsWith("aria-")) continue
            // Skip invalid aria-* names — those are reported by AriaPropsRule.
            if (normalized !in AriaConstants.VALID_ARIA_ATTRIBUTES) continue
            // Supported by this role (own + required + global) — fine.
            if (normalized in supportedProps) continue

            holder.registerProblem(
                attr,
                "Accessibility: role='$roleValue' does not support the '$normalized' attribute.",
                RemoveUnsupportedAriaPropQuickFix()
            )
        }
    }
}

/**
 * QuickFix: removes the ARIA attribute that is not supported by the role.
 */
private class RemoveUnsupportedAriaPropQuickFix : LocalQuickFix {
    override fun getName(): String = "Remove unsupported ARIA attribute"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val attr = descriptor.psiElement
        if (attr is XmlAttribute && attr.isValid) {
            attr.delete()
        }
    }
}


