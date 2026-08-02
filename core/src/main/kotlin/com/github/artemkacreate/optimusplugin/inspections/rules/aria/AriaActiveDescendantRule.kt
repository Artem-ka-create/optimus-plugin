package com.github.artemkacreate.optimusplugin.inspections.rules.aria

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.TechnologyType
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.github.artemkacreate.optimusplugin.inspections.util.TagClassifier.getFileTechnologyType
import com.github.artemkacreate.optimusplugin.inspections.util.TagNavigator.nativeTagNameOrNull
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

class AriaActiveDescendantRule : AccessibilityRule {

    override val id: String = "ariaActiveDescendantRule"
    override val displayName: String =
        "aria-activedescendant must be on an element with a valid interactive role and be focusable"

    companion object {
        private const val INVALID_ROLE_MESSAGE =
            "Accessibility: 'aria-activedescendant' should not be used on <%s> without an appropriate interactive role (e.g. role='listbox' or role='combobox')."
        private const val MISSING_TABINDEX_MESSAGE =
            "Accessibility: <%s> with role='%s' and 'aria-activedescendant' must have a focusable 'tabindex' (>= 0) so the user can focus it via keyboard."

        val VALID_NATIVE_TAGS = setOf(
            "input", "textarea", "select"
        )

        val VALID_ROLES = setOf(
            "combobox", "listbox", "menu", "menubar", "tree", "grid", "treegrid", "application", "searchbox"
        )
    }

    override fun checkElementByRule(
        element: PsiElement, file: PsiFile, holder: ProblemsHolder
    ) {
        if (element !is XmlTag) return

        val attributes = element.attributes
        val activeDescendantAttr = attributes.find {
            AttributeResolver.normalizeAttrName(it.name) == "aria-activedescendant"
        } ?: return

        val tagName = element.name.lowercase().trim()

        val isNativeValid = element.nativeTagNameOrNull() in VALID_NATIVE_TAGS
        if (isNativeValid) return

        val roleAttr = attributes.find { AttributeResolver.normalizeAttrName(it.name) == "role" }
        val roleAttrValue = roleAttr?.let { AttributeResolver.resolveAttributeValue(it)?.lowercase()?.trim() }

        // If a role is present but its value cannot be resolved statically
        // (e.g. a dynamic binding like :role="expr" / role={expr}), skip to
        // avoid false positives — it may resolve to a valid role at runtime.
        if (roleAttr != null && roleAttrValue == null) return

        val isRoleValid = roleAttrValue != null && VALID_ROLES.contains(roleAttrValue)

        if (!isRoleValid) {
            holder.registerProblem(
                activeDescendantAttr, INVALID_ROLE_MESSAGE.format(tagName)
            )
            return
        }

        val tabIndexAttr = attributes.find {
            AttributeResolver.normalizeAttrName(it.name) == "tabindex"
        }
        val tabIndexValue = tabIndexAttr?.value?.let { AttributeResolver.parseNumericValue(it) }

        // If tabindex is present but its value cannot be resolved statically
        // (e.g. a dynamic binding like :tabindex="idx" / [tabindex]="expr"),
        // skip to avoid false positives — it may be >= 0 at runtime.
        if (tabIndexAttr != null && tabIndexValue == null) return

        // Focusable only when tabindex is present with a value >= 0.
        // Missing tabindex or tabindex="-1" are flagged.
        val isFocusable = tabIndexValue != null && tabIndexValue >= 0

        if (!isFocusable) {
            holder.registerProblem(
                element,
                MISSING_TABINDEX_MESSAGE.format(tagName, roleAttrValue),
                getFixesByPsFile(file)
            )
        }
    }

    private fun getFixesByPsFile(psiFile: PsiFile): LocalQuickFix {
        return when (psiFile.getFileTechnologyType()) {
            TechnologyType.VUE -> AddVueAriaActiveDescendantTabIndexZeroQuickFix()
            TechnologyType.REACT -> AddReactAriaActiveDescendantTabIndexZeroQuickFix()
            TechnologyType.ANGULAR -> AddAngularAriaActiveDescendantTabIndexZeroQuickFix()
            else -> AddAriaActiveDescendantTabIndexZeroQuickFix()
        }
    }
}

private class AddAriaActiveDescendantTabIndexZeroQuickFix : LocalQuickFix {
    override fun getName(): String = "Add tabindex=\"0\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("tabindex", "0")
        }
    }
}

private class AddReactAriaActiveDescendantTabIndexZeroQuickFix : LocalQuickFix {
    override fun getName(): String = "Add tabIndex=\"0\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("tabIndex", "0")
        }
    }
}

private class AddVueAriaActiveDescendantTabIndexZeroQuickFix : LocalQuickFix {
    override fun getName(): String = "Add :tabindex=\"0\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute(":tabindex", "0")
        }
    }
}

private class AddAngularAriaActiveDescendantTabIndexZeroQuickFix : LocalQuickFix {
    override fun getName(): String = "Add [tabindex]=\"0\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("[tabindex]", "0")
        }
    }
}
