package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.RuleCategory
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver.normalizeAttrName
import com.github.artemkacreate.optimusplugin.inspections.util.constants.AriaConstants
import com.github.artemkacreate.optimusplugin.inspections.util.constants.RoleTagConstants
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

class RoleHasRequiredAriaPropsRule : AccessibilityRule {

    override val id = "roleHasRequiredAriaProps"
    override val displayName = "Elements with an ARIA role must have all required ARIA attributes"
    override val category = RuleCategory.ROLE
    override val description = "Elements with an ARIA role must include all required ARIA attributes for that role."

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return

        val roleAttr = element.attributes.firstOrNull {
            normalizeAttrName(it.name) == AriaConstants.ARIA_ROLE_ATTRIBUTE
        } ?: return

        // Resolve the role value (handles Vue ':role', Angular '[attr.role]', JSX role={"x"}).
        // Fall back to the raw value if it looks like a simple role identifier.
        val roleValue = (AttributeResolver.resolveAttributeValue(roleAttr)
            ?: roleAttr.value?.takeIf { it.isNotBlank() }
            ?: return)
            .trim()
            .lowercase()

        val requiredProps = RoleTagConstants.ROLE_REQUIRED_ARIA_PROPS.get(roleValue) ?: return

        val presentAttrs = element.attributes
            .map { normalizeAttrName(it.name) }
            .toSet()

        val missing = requiredProps.filter { it !in presentAttrs }
        if (missing.isEmpty()) return

        holder.registerProblem(
            roleAttr,
            "Accessibility: role='$roleValue' requires ${missing.joinToString(", ")}.",
            AddRequiredAriaPropsQuickFix(missing)
        )
    }
}

/**
 * QuickFix: adds the missing required ARIA attribute(s) with an empty value so
 * the developer can fill in the correct state.
 */
private class AddRequiredAriaPropsQuickFix(
    private val missing: List<String>
) : LocalQuickFix {
    override fun getName(): String =
        "Add missing ARIA attribute(s): ${missing.joinToString(", ")}"

    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val tag = descriptor.psiElement?.parent as? XmlTag ?: return
        if (!tag.isValid) return
        missing.forEach { attr -> tag.setAttribute(attr, "") }
    }
}


