package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
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
 * Supports static alt and dynamic bindings (:alt, v-bind:alt, \[alt\]) with literal string values.
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
        private val DYNAMIC_PREFIXES = setOf(":", "v-bind:", "[")
        private const val MESSAGE =
            "Accessibility: <img> alt should not contain redundant words like 'image', 'photo', 'picture'"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.name.equals("img", ignoreCase = true)) return

        val altAttr = element.attributes.find { it.name.lowercase() in ALT_ATTRIBUTES } ?: return
        val attrName = altAttr.name.lowercase()
        val rawValue = altAttr.value ?: return

        // For dynamic bindings, try to extract literal
        val isDynamic = DYNAMIC_PREFIXES.any { attrName.startsWith(it) }
        val altValue = if (isDynamic) extractLiteralFromBinding(rawValue) else rawValue
        altValue ?: return

        // Check if alt contains any redundant words
        val altLower = altValue.trim().lowercase()
        if (REDUNDANT_WORDS.any { altLower.contains(it) }) {
            holder.registerProblem(element, MESSAGE, ClearRedundantAltQuickFix())
        }
    }

    /**
     * Tries to extract a string literal from a dynamic binding value.
     * E.g., "'photo of cat'" → "photo of cat"
     * Returns null if the value is a complex expression.
     */
    private fun extractLiteralFromBinding(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.length >= 2) {
            val first = trimmed.first()
            val last = trimmed.last()
            if ((first == '\'' && last == '\'') || (first == '"' && last == '"')) {
                return trimmed.substring(1, trimmed.length - 1)
            }
        }
        // Check for template literal with backticks
        if (trimmed.startsWith('`') && trimmed.endsWith('`') && !trimmed.contains("\${")) {
            return trimmed.substring(1, trimmed.length - 1)
        }
        return null
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
