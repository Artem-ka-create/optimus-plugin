package com.github.artemkacreate.optimusplugin.inspections.rules.anchor

import com.github.artemkacreate.optimusplugin.inspections.accessibility.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.isHtmlTag
import com.github.artemkacreate.optimusplugin.inspections.util.ContentInspector.collectNestedText

import com.github.artemkacreate.optimusplugin.inspections.util.constants.AriaConstants
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

class AnchorAmbigousTextRule : AccessibilityRule {

    override val id = "anchorAmbiguousText"
    override val displayName = "Anchor Ambiguous Text"

    companion object {
        private const val MESSAGE =
            "Accessibility: <a> has ambiguous text. Use descriptive link text so it makes sense out of context."

        val AMBIGUOUS_WORDS = setOf(
            "here", "click here", "click", "link", "go", "go here", "url", "website", "page", "button", "this", "this link",
            "more", "learn more", "read more", "see more", "view more", "find out more", "get more", "more info", "more information",
            "details", "get details", "view details", "continue", "continue reading", "read on",
            "download", "download here", "download now", "file", "open", "open here", "view", "pdf",
            "start", "get started", "try now", "buy now", "order now", "register", "sign up", "submit", "next", "back", "previous"
        )
    }

    override fun checkElementByRule(
        element: PsiElement,
        file: PsiFile,
        holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return
        if (!element.isHtmlTag("a")) return

        val ariaLabelAttribute = element.attributes.find {
            AttributeResolver.normalizeAttrName(it.name) in AriaConstants.ARIA_LABEL_ATTRIBUTES
        }
        val ariaLabelValue = ariaLabelAttribute?.let { AttributeResolver.resolveAttributeValue(it) } ?: ""

        val (textToValidate, targetForHighlight) = if (ariaLabelValue.isNotBlank()) {
            ariaLabelValue to (ariaLabelAttribute ?: element)
        } else {
            element.collectNestedText() to element
        }

        if (isAmbiguous(textToValidate)) {
            holder.registerProblem(targetForHighlight, MESSAGE)
        }
    }

    private fun isAmbiguous(text: String): Boolean {
        val normalized = normalize(text)
        return normalized.isNotBlank() && normalized in AMBIGUOUS_WORDS
    }

    /**
     * Lowercases, collapses whitespace and strips surrounding punctuation so that
     * values like "Click Here!" or "  read  more " are matched against the word set.
     */
    private fun normalize(text: String): String {
        return text
            .lowercase()
            .replace(Regex("[{}]"), "")
            .replace(Regex("[^a-z0-9\\s]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
