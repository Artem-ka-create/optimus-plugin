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
import com.intellij.psi.xml.XmlTag

/**
 * Rule: <img> alt attribute should not contain redundant words like "image", "photo", "picture".
 * Screen readers already announce the element as an image, so these words add no value.
 * Supports static alt and dynamic bindings (:alt, v-bind:alt, [alt]) with literal string values.
 */
class ImgRedundantAltRule : AccessibilityRule {

    override val id = "imgRedundantAlt"
    override val displayName = "img alt should not contain redundant words"
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX,
        FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        private val REDUNDANT_WORDS = setOf("image", "photo", "picture", "icon", "graphic")
        private val ALT_ATTRIBUTES = setOf("alt", ":alt", "v-bind:alt", "[alt]")
        private const val MESSAGE =
            "Accessibility: <img> alt should not contain redundant words like 'image', 'photo', 'picture'"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.name.equals("img", ignoreCase = true)) return

        val altAttr = element.attributes.find { it.name.lowercase() in ALT_ATTRIBUTES } ?: return

        // Resolve value (handles dynamic bindings automatically)
        val altValue = ExtractionTool.resolveAttributeValue(altAttr) ?: return

        // Check if alt contains any redundant words
        val altLower = altValue.trim().lowercase()
        if (REDUNDANT_WORDS.any { altLower.contains(it) }) {
            holder.registerProblem(element, MESSAGE, ClearRedundantAltQuickFix())
        }
    }
}

/**
 * QuickFix: clears redundant alt text so the developer can replace it with a meaningful description
 */
class ClearRedundantAltQuickFix : LocalQuickFix {
    override fun getName(): String = "Clear redundant alt text attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("alt", "")
        }
    }
}
