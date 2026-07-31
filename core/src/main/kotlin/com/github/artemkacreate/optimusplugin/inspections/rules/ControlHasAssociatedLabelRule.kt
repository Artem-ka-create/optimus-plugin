package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.github.artemkacreate.optimusplugin.inspections.util.ContentInspector.containsXmlTextNonRecursive
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.nativeTagNameOrNull
import com.github.artemkacreate.optimusplugin.inspections.util.constants.AriaConstants
import com.github.artemkacreate.optimusplugin.inspections.util.constants.HtmlConstants
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag

class ControlHasAssociatedLabelRule : AccessibilityRule {

    override val id: String = "controlHasAssociatedLabel"
    override val displayName: String = "Control tag should have associated label"

    companion object {
        private const val FIELD_MESSAGE =
            "Accessibility: Form control (<input>/<select>/<textarea>) should have an associated <label>, aria-label or aria-labelledby"
        private const val INNER_TEXT_MESSAGE =
            "Accessibility: <button>/<a> should have inner text content, aria-label or aria-labelledby"
        private const val EMPTY_ARIA_LABEL_MESSAGE =
            "Accessibility: aria-label / aria-labelledby is present but empty — provide a meaningful value"
        private val FIELD_TAGS = setOf("input", "select", "textarea")
        private val INNER_TEXT_TAGS = setOf("button", "a")
        private val ID_ATTRIBUTES = setOf("id")
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        val tagName = element.nativeTagNameOrNull() ?: return
        val isInteractiveTag = tagName in HtmlConstants.LABEL_REQUIRED_NATIVE_TAGS
        if (!isInteractiveTag) return

        // 1. Aria label handling
        val ariaLabelAttributes = element.attributes
            .filter { AttributeResolver.normalizeAttrName(it.name) in AriaConstants.ARIA_LABEL_ATTRIBUTES }

        // a) present with a non-empty value → already labelled → OK
        if (ariaLabelAttributes.any { !AttributeResolver.resolveAttributeValue(it).isNullOrBlank() }) return

        // b) present but empty → dedicated message
        if (ariaLabelAttributes.isNotEmpty()) {
            holder.registerProblem(element, EMPTY_ARIA_LABEL_MESSAGE)
            return
        }

        // 2. Form fields (input / select / textarea) are OK when they have a connected label
        if (tagName in FIELD_TAGS) {
            if (!hasConnectedLabel(element, file)) {
                holder.registerProblem(element, FIELD_MESSAGE)
            }
            return
        }

        // 3. button / <a> are OK when they contain inner text content
        if (tagName in INNER_TEXT_TAGS) {
            if (!element.containsXmlTextNonRecursive()) {
                holder.registerProblem(element, INNER_TEXT_MESSAGE)
            }
            return
        }
    }

    /**
     * A field is considered to have a connected label when:
     *  - it is nested inside a <label> tag, or
     *  - there is a <label for="..."> in the file whose value matches this element's id.
     */
    private fun hasConnectedLabel(element: XmlTag, file: PsiFile): Boolean {
        // a) nested inside a <label>
        if (TagNavigator.isNestedInsideTag(element, "label")) return true

        // b) a <label for="id"> that references this element's id
        val elementId = element.attributes
            .find { AttributeResolver.normalizeAttrName(it.name) in ID_ATTRIBUTES }
            ?.let { AttributeResolver.resolveAttributeValue(it)?.trim() }
            ?.takeIf { it.isNotBlank() }
            ?: return false

        return PsiTreeUtil.findChildrenOfType(file, XmlTag::class.java)
            .filter { it.name.equals("label", true) }
            .any { label ->
                label.attributes
                    .find { AttributeResolver.normalizeAttrName(it.name) in HtmlConstants.FOR_ATTRIBUTES }
                    ?.let { AttributeResolver.resolveAttributeValue(it)?.trim() } == elementId
            }
    }
}
