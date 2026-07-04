package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.util.CommonValues
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.normalizeAttrName
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

/**
 * Rule: The `role` attribute value must be a valid WAI-ARIA role.
 * Checks that the value of the `role` attribute (after resolving
 * Vue/Angular/JSX bindings) is one of the recognized WAI-ARIA roles.
 *
 * Works with:
 * - HTML: role="button"
 * - Vue: :role="'button'" / v-bind:role="'dialog'"
 * - Angular: \[attr.role\]="'navigation'" / \[role\]="'alert'"
 * - React/JSX: role={"button"} / role="button"
 *
 * Examples of invalid: role="buton", role="nav", role="header"
 * Examples of valid: role="button", role="navigation", role="banner"
 */
class AriaRoleRule : AccessibilityRule {

    override val id = "ariaRoles"
    override val displayName = "Role attribute value must be a valid WAI-ARIA role"
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX,
        FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        for (attr in element.attributes) {
            val normalized = normalizeAttrName(attr.name)
            if (normalized != CommonValues.ARIA_ROLE_ATTRIBUTE) continue

            // Resolve the value (handles Vue ':role', Angular '[attr.role]', JSX role={"x"})
            // If resolveAttributeValue returns null (dynamic expression), fall back to raw value
            // if it looks like a simple role identifier (e.g., "button", "nav", "buton")
            val roleValue = ExtractionTool.resolveAttributeValue(attr)
                ?: attr.value?.takeIf { it.matches(Regex("^[a-z][a-z-]*$")) }
                ?: continue  // complex expression (function call, variable with dots, etc.) — skip

            if (roleValue.isBlank() || roleValue !in CommonValues.VALID_ARIA_ROLE_VALUES) {
                holder.registerProblem(
                    attr,
                    "Accessibility: '$roleValue' is not a valid ARIA role.",
                    RemoveInvalidAriaRoleAttrQuickFix()
                )
            }
        }
    }
}

/**
 * QuickFix: clears the invalid role attribute value.
 */
class RemoveInvalidAriaRoleAttrQuickFix : LocalQuickFix {
    override fun getName(): String = "Remove invalid ARIA role value"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val attr = descriptor.psiElement
        if (attr is XmlAttribute && attr.isValid) {
            attr.setValue("")
        }
    }
}
