package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.enums.TechnologyType
import com.github.artemkacreate.optimusplugin.inspections.util.CommonValues
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.containsXmlTextNonRecursive
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.getFileTechnologyType
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
        private const val MESSAGE_ACCESSIBILITY_CONTENT = "Accessibility: Label should have any accessibility content."
        private val NESTED_FIELD_TAGS = setOf("input", "select", "textarea")
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        // Only apply to <label> and <output> tags
        val tagName = ExtractionTool.normalizeAttrName(element.name)
        if (tagName !in CommonValues.FIELD_LABEL_ATTRIBUTES) return

        val forAttribute =
            element.attributes.find { ExtractionTool.normalizeAttrName(it.name) in CommonValues.FOR_ATTRIBUTES }

        val forAttributeValueExists =
            forAttribute != null && !ExtractionTool.resolveAttributeValue(forAttribute).isNullOrBlank()
        if (forAttributeValueExists) return
        val hasNestedFields = ExtractionTool.hasNestedTag(element, NESTED_FIELD_TAGS)

        if (hasNestedFields) {
            val hasAccessibleTextCont = element.containsXmlTextNonRecursive()
            if (hasAccessibleTextCont) {
                return
            } else {
                holder.registerProblem(
                    element, MESSAGE_ACCESSIBILITY_CONTENT, *getFixesByPsFile(file, true)
                )
            }
            return
        }
        holder.registerProblem(
            element, MESSAGE, *getFixesByPsFile(file, false)
        )
    }


    private fun getFixesByPsFile(psiFile: PsiFile, hasNestedField: Boolean): Array<LocalQuickFix> {
        val fileType = psiFile.getFileTechnologyType()

        val attributeFix: LocalQuickFix = when (fileType) {
            TechnologyType.VUE -> AddHtmlForVueAttributeQuickFix()
            TechnologyType.REACT -> AddHtmlForReactAttributeQuickFix()
            TechnologyType.ANGULAR -> AddHtmlForAngularAttributeQuickFix()
            else -> AddForVanilaHtmlAttributeQuickFix()
        }

        val nestedTagFix: LocalQuickFix = if (hasNestedField) AddNestedSpanTextQuickFix() else AddNestedInputQuickFix()

        return arrayOf(attributeFix, nestedTagFix)
    }
}

/**
 * QuickFix: adds a `for=""` attribute to the tag (HTML / Vue).
 */
class AddForVanilaHtmlAttributeQuickFix : LocalQuickFix {
    override fun getName(): String = "Add for=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("for", "generatedId")
        }
    }
}

/**
 * QuickFix: adds an `htmlFor=""` attribute to the tag (JSX / React).
 */
class AddHtmlForReactAttributeQuickFix : LocalQuickFix {
    override fun getName(): String = "Add htmlFor=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("htmlFor", "generatedId")
        }
    }
}

/**
 * QuickFix: adds an `:for=""` attribute to the tag (JSX / React).
 */
class AddHtmlForVueAttributeQuickFix : LocalQuickFix {
    override fun getName(): String = "Add htmlFor=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute(":for", "generatedId")
        }
    }
}

/**
 * QuickFix: adds an `[for]=""` attribute to the tag (JSX / React).
 */
class AddHtmlForAngularAttributeQuickFix : LocalQuickFix {
    override fun getName(): String = "Add htmlFor=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("[for]", "generatedId")
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

/**
 * QuickFix: adds a nested `<input />` field inside the tag.
 */
class AddNestedSpanTextQuickFix : LocalQuickFix {
    override fun getName(): String = "Add nested <span> field"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            val span = element.createChildTag("span", element.namespace, "generated Content", true)
            element.addSubTag(span, false)
        }
    }
}
