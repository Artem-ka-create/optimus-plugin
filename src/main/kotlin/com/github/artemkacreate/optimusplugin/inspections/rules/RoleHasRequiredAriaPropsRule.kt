package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.CommonValues
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.normalizeAttrName
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

/**
 * Rule: An element with an explicit ARIA `role` must also declare every ARIA
 * state/property that the WAI-ARIA specification marks as *required* for that
 * role.
 *
 * Some roles are meaningless to assistive technologies without their required
 * attributes. For example, `role="checkbox"` describes a checkbox but does not
 * say whether it is checked — that information lives in `aria-checked`. Screen
 * readers will announce "checkbox" but cannot tell the user its state, so the
 * widget is effectively broken for them.
 *
 * Works with:
 * - HTML: role="checkbox"
 * - Vue: :role="'checkbox'" / v-bind:role="'checkbox'"
 * - Angular: \[attr.role\]="'checkbox'" / \[role\]="'checkbox'"
 * - React/JSX: role={"checkbox"} / role="checkbox"
 *
 * Examples of invalid: <div role="checkbox"></div>  (missing aria-checked)
 *                      <div role="slider"></div>     (missing aria-valuenow)
 * Examples of valid:   <div role="checkbox" aria-checked="false"></div>
 *                      <div role="slider" aria-valuenow="5"></div>
 */
class RoleHasRequiredAriaPropsRule : AccessibilityRule {

    override val id = "roleHasRequiredAriaProps"
    override val displayName = "Elements with an ARIA role must have all required ARIA attributes"

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return

        val roleAttr = element.attributes.firstOrNull {
            normalizeAttrName(it.name) == CommonValues.ARIA_ROLE_ATTRIBUTE
        } ?: return

        // Resolve the role value (handles Vue ':role', Angular '[attr.role]', JSX role={"x"}).
        // Fall back to the raw value if it looks like a simple role identifier.
        val roleValue = (ExtractionTool.resolveAttributeValue(roleAttr)
            ?: roleAttr.value?.takeIf { it.isNotBlank() }
            ?: return)
            .trim()
            .lowercase()

        val requiredProps = CommonValues.ROLE_REQUIRED_ARIA_PROPS.get(roleValue) ?: return

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


