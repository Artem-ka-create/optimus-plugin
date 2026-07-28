package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.constants.AriaConstants
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.nativeTagNameOrNull
import com.github.artemkacreate.optimusplugin.inspections.util.constants.RoleTagConstants
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

/**
 * Rule: prefer a native HTML element over an equivalent ARIA role.
 * E.g. <div role="button"> should be <button>, <span role="navigation"> should be <nav>.
 *
 * When the element is ALREADY the native tag for that role, this rule stays silent —
 * that redundant case is handled by NoRedudantRolesRule.
 * Mirrors eslint jsx-a11y "prefer-tag-over-role".
 */
class PreferTagOverRoleRule : AccessibilityRule {
    override val id: String = "preferTagOverRole"
    override val displayName: String = "Prefer native HTML tag over ARIA role"

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return
        val tagName = element.nativeTagNameOrNull() ?: return

        val roleAttribute = element.attributes.find {
            ExtractionTool.normalizeAttrName(it.name) == AriaConstants.ARIA_ROLE_ATTRIBUTE
        } ?: return

        val roleValue = ExtractionTool.resolveAttributeValue(roleAttribute)
            ?.lowercase()?.trim()?.split(Regex("\\s+"))?.firstOrNull() ?: return

        val preferredTags = RoleTagConstants.ROLE_TO_PREFERRED_TAGS[roleValue] ?: return

        // Skip if the element is ALREADY a native tag for this role (redundant-roles' job).
        val baseTags = preferredTags.map { it.substringBefore("[") }
        if (tagName in baseTags) return

        val message = "Accessibility: prefer using ${preferredTags.joinToString(" / ") { "<$it>" }} " +
            "instead of role=\"$roleValue\"."

        // Offer an auto-fix ONLY when we can pick a single, attribute-free native tag:
        //  - exactly one preferred tag (e.g. navigation → nav), or
        //  - a preferred tag whose name equals the role (the canonical tag, e.g. button,
        //    even though `summary` also has role=button).
        // Roles like heading (needs a level), link (needs href) or checkbox
        // (input[type=checkbox]) can't be converted safely → message only.
        val safeTag = preferredTags.singleOrNull()?.takeIf { !it.contains("[") }
            ?: preferredTags.firstOrNull { it == roleValue }
        if (safeTag != null) {
            holder.registerProblem(roleAttribute, message, ConvertToTagQuickFix(safeTag))
        } else {
            holder.registerProblem(roleAttribute, message)
        }
    }
}

private class ConvertToTagQuickFix(private val tag: String) : LocalQuickFix {
    override fun getName(): String = "Change element to <$tag> and remove role"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val attr = descriptor.psiElement as? XmlAttribute ?: return
        val tagEl = attr.parent
        if (tagEl !is XmlTag || !tagEl.isValid) return
        attr.delete()
        tagEl.name = tag
    }
}
