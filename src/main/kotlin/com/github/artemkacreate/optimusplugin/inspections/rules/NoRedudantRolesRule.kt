package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.util.CommonValues
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool
import com.github.artemkacreate.optimusplugin.inspections.util.ExtractionTool.nativeTagNameOrNull
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

class NoRedudantRolesRule : AccessibilityRule {
    override val id: String = "noRedudantRolesRule"
    override val displayName: String = "Redundant ARIA role"
    override val supportedExtensions: Set<FileExtension> = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX, FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        /** header→banner and footer→contentinfo only apply outside these sectioning elements. */
        private val SECTIONING_ANCESTORS = setOf("article", "aside", "main", "nav", "section")
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return
        val tagName = element.nativeTagNameOrNull() ?: return

        val roleAttribute =
            element.attributes.find { ExtractionTool.normalizeAttrName(it.name) == CommonValues.ARIA_ROLE_ATTRIBUTE }
                ?: return
        val roleValue = ExtractionTool.resolveAttributeValue(roleAttribute)?.lowercase()?.trim()
        val expectedImplicitRole = when (tagName) {
            "a" -> {
                val hasHref = element.getAttribute("href") != null || element.attributes.any {
                    val clean = ExtractionTool.normalizeAttrName(it.name)
                    clean == "routerlink" || clean == "to"
                }
                if (hasHref) "link" else null
            }

            "input" -> {
                val type = element.getAttributeValue("type")?.lowercase()?.trim() ?: "text"
                when (type) {
                    "button", "submit", "reset", "image" -> "button"
                    "checkbox" -> "checkbox"
                    "radio" -> "radio"
                    "range" -> "slider"
                    "number" -> "spinbutton"
                    "search" -> "searchbox"
                    "text", "email", "tel", "url" -> "textbox"
                    // password, file, color, date/time, hidden, etc. have no simple implicit role
                    else -> null
                }
            }

            // header→banner / footer→contentinfo only when NOT inside sectioning content.
            "header", "footer" -> {
                if (SECTIONING_ANCESTORS.any { ExtractionTool.isNestedInsideTag(element, it) }) null
                else CommonValues.REDUNDANT_TAGS_ROLES_MAP[tagName]
            }

            else -> CommonValues.REDUNDANT_TAGS_ROLES_MAP[tagName]
        }

        if (!expectedImplicitRole.isNullOrBlank() && expectedImplicitRole.equals(roleValue, true)) {

            holder.registerProblem(
                roleAttribute,
                "Accessibility: The role '$roleValue' is redundant for the <$tagName> element, as it is already implied by native HTML semantics.",
                NoRedudantRolesRuleQuickFix()

            )
        }
    }
}

private class NoRedudantRolesRuleQuickFix : LocalQuickFix {
    override fun getName(): String = "Delete redundant role attribute"
    override fun getFamilyName(): String = "Accessibility fixes"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val roleAttribute = descriptor.psiElement
        if (roleAttribute is XmlAttribute && roleAttribute.isValid) {
            roleAttribute.delete()
        }
    }
}
