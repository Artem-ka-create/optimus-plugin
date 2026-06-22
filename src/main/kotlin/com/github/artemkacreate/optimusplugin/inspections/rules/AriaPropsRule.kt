package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.util.CommonValues
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
 * Rule: All aria-* attributes must be valid WAI-ARIA attributes.
 * Checks that any attribute starting with "aria-" (after normalization of
 * Vue/Angular/JSX bindings) exists in the WAI-ARIA specification.
 *
 * Examples of invalid: aria-labelled, aria-role, aria-hiden
 * Examples of valid: aria-label, aria-hidden, aria-checked
 */
class AriaPropsRule : AccessibilityRule {

    override val id = "ariaProps"
    override val displayName = "ARIA attributes must be valid based on W3C, WAI-ARIA, MDN"
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
            if (!normalized.startsWith("aria-")) continue

            if (normalized !in CommonValues.VALID_ARIA_ATTRIBUTES) {
                holder.registerProblem(
                    attr,
                    "Accessibility: '$normalized' is not a valid ARIA attribute.",
                    RemoveInvalidAriaAttrQuickFix()
                )
            }
        }
    }
}

/**
 * QuickFix: removes the invalid ARIA attribute from the element.
 */
class RemoveInvalidAriaAttrQuickFix : LocalQuickFix {
    override fun getName(): String = "Remove invalid ARIA attribute"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val attr = descriptor.psiElement
        if (attr is XmlAttribute && attr.isValid) {
            attr.delete()
        }
    }
}
