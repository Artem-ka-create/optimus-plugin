package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

/**
 * Rule: tabindex should not be greater than 0.
 * Positive tabindex values disrupt the natural tab order and create usability issues.
 * Use tabindex="0" (natural order) or tabindex="-1" (programmatic focus only).
 * Supports Vue (:tabindex, v-bind:tabindex) and Angular ([attr.tabindex]) bindings.
 */
class TabIndexNoPositiveRule : AccessibilityRule {

    override val id = "tabIndexNoPositive"
    override val displayName = "Element should not have positive tabindex"
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX,
        FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        private val TABINDEX_ATTRIBUTES = setOf(
            "tabindex",                            // HTML standard
            ":tabindex", "v-bind:tabindex",        // Vue
            "[tabindex]", "[attr.tabindex]"        // Angular
        )
        private const val MESSAGE = "Accessibility: tabindex should not be greater than 0"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return

        // Check ALL tabindex-related attributes (element may have both tabIndex and tabindex)
        val tabindexAttrs = element.attributes.filter { it.name.lowercase() in TABINDEX_ATTRIBUTES }
        if (tabindexAttrs.isEmpty()) return

        val hasPositive = tabindexAttrs.any { attr ->
            val resolved = ExtractionTool.resolveAttributeValue(attr) ?: return@any false
            val numericValue = resolved.trim().toIntOrNull() ?: return@any false
            numericValue > 0
        }

        if (hasPositive) {
            holder.registerProblem(element, MESSAGE, SetTabIndexToZeroQuickFix())
        }
    }
}

/**
 * QuickFix: finds tabindex attributes with positive values and sets them to "0"
 */
class SetTabIndexToZeroQuickFix : LocalQuickFix {
    override fun getName(): String = "Set tabindex to \"0\""
    override fun getFamilyName(): String = "Accessibility fixes"

    companion object {
        private val TABINDEX_ATTRIBUTES = setOf(
            "tabindex", ":tabindex", "v-bind:tabindex",
            "[tabindex]", "[attr.tabindex]"
        )
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element !is XmlTag || !element.isValid) return

        // Find all tabindex attributes with positive values and set them to "0"
        element.attributes
            .filter { it.name.lowercase() in TABINDEX_ATTRIBUTES }
            .filter { attr: XmlAttribute -> (ExtractionTool.parseNumericValue(attr.value ?: "") ?: 0) > 0 }
            .forEach { attr: XmlAttribute -> attr.setValue("0") }
    }
}
