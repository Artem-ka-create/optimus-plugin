package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.util.CommonValues
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

class LabelHasForRule : AccessibilityRule {

    override val id: String = "labelHasForRule"
    override val displayName: String = "Label should have 'for' attribute ot have nested field"
    override val supportedExtensions: Set<FileExtension> = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX, FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        private const val MESSAGE = "Accessibility: Label should have 'for' attribute ot have nested field"
        private val NESTED_FIELD_TAGS = setOf("input", "select", "textarea")
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        // Only apply to <label> and <output> tags
        val tagName = ExtractionTool.normalizeAttrName(element.name)
        if (tagName !in CommonValues.FIELD_LABEL_ATTRIBUTES) return

        // 1. A valid "for"/"htmlFor" attribute satisfies the rule
        // TODO: in the future check weather attributes with ids in for attribute exist in psFile
        val forAttribute =
            element.attributes.find { ExtractionTool.normalizeAttrName(it.name) in CommonValues.FOR_ATTRIBUTES }

        val forAttributeValueExists = forAttribute != null && !ExtractionTool.resolveAttributeValue(forAttribute).isNullOrBlank()
        val hsNestedFields = hasNestedField(element)

        // A valid "for"/"htmlFor" attribute OR a nested field satisfies the rule
        if (forAttributeValueExists || hsNestedFields) return

        // Otherwise the label is inaccessible
        holder.registerProblem(
            element,
            MESSAGE,
            AddForAttributeQuickFix(),
            AddHtmlForAttributeQuickFix(),
            AddNestedInputQuickFix()
        )
    }

    private fun hasNestedField(label: XmlTag): Boolean {
        return generateSequence(label.subTags.asList()) { tags ->
            tags.flatMap { it.subTags.asList() }.takeIf { it.isNotEmpty() }
        }.flatten().any { it.name.lowercase() in NESTED_FIELD_TAGS }
    }
}

/**
 * QuickFix: adds a `for=""` attribute to the tag (HTML / Vue).
 */
class AddForAttributeQuickFix : LocalQuickFix {
    override fun getName(): String = "Add for=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("for", "")
        }
    }
}

/**
 * QuickFix: adds an `htmlFor=""` attribute to the tag (JSX / React).
 */
class AddHtmlForAttributeQuickFix : LocalQuickFix {
    override fun getName(): String = "Add htmlFor=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("htmlFor", "")
        }
    }
}

/**
 * QuickFix: adds a nested `<input />` field inside the tag.
 */
class AddNestedInputQuickFix : LocalQuickFix {
    override fun getName(): String = "Add nested <input> field"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            val input = element.createChildTag("input", element.namespace, null, false)
            element.addSubTag(input, false)
        }
    }
}

