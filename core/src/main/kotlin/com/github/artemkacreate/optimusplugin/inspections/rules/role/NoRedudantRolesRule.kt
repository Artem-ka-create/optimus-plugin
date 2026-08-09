package com.github.artemkacreate.optimusplugin.inspections.rules.role

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.RuleCategory
import com.github.artemkacreate.optimusplugin.inspections.fixes.RemoveAttributeQuickFix
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.nativeTagNameOrNull
import com.github.artemkacreate.optimusplugin.inspections.util.constants.AriaConstants
import com.github.artemkacreate.optimusplugin.inspections.util.constants.RoleTagConstants
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

class NoRedudantRolesRule : AccessibilityRule {
    override val id: String = "noRedudantRolesRule"
    override val displayName: String = "Redundant ARIA role"
    override val category = RuleCategory.ROLE

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
            element.attributes.find { AttributeResolver.normalizeAttrName(it.name) == AriaConstants.ARIA_ROLE_ATTRIBUTE }
                ?: return
        val roleValue = AttributeResolver.resolveAttributeValue(roleAttribute)?.lowercase()?.trim()
        val expectedImplicitRole = when (tagName) {
            "a" -> {
                val hasHref = element.getAttribute("href") != null || element.attributes.any {
                    val clean = AttributeResolver.normalizeAttrName(it.name)
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
                if (SECTIONING_ANCESTORS.any { TagNavigator.isNestedInsideTag(element, it) }) null
                else RoleTagConstants.REDUNDANT_TAGS_ROLES_MAP[tagName]
            }

            else -> RoleTagConstants.REDUNDANT_TAGS_ROLES_MAP[tagName]
        }

        if (!expectedImplicitRole.isNullOrBlank() && expectedImplicitRole.equals(roleValue, true)) {

            holder.registerProblem(
                roleAttribute,
                "Accessibility: The role '$roleValue' is redundant for the <$tagName> element, as it is already implied by native HTML semantics.",
                RemoveAttributeQuickFix("Delete redundant role attribute")
            )
        }
    }
}
