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
 * Rule: <a> must have a valid href attribute value.
 * Flags href="#", href="", and href="javascript:..." as invalid.
 * Skips anchors without href (may be dynamic SPA links) and dynamic bindings like :href or [href].
 */
class AnchorIsValidRule : AccessibilityRule {

    override val id = "anchorIsValid"
    override val displayName = "Anchor must have valid href"
    override val supportedExtensions = setOf(
        FileExtension.HTML, FileExtension.JS, FileExtension.JSX,
        FileExtension.TS, FileExtension.TSX, FileExtension.VUE
    )

    companion object {
        /** Only check plain "href" — dynamic bindings (:href, [href]) can't be validated statically */
        private const val HREF_ATTR = "href"
        private const val MESSAGE = "Accessibility: <a> must have a valid href (not '#', empty, or 'javascript:')"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return
        if (!element.name.equals("a", ignoreCase = true)) return

        // Only check if a static "href" attribute exists — skip if missing entirely
        val hrefAttr = element.getAttribute(HREF_ATTR) ?: return

        val hrefValue = hrefAttr.value

        if (hrefValue.isNullOrBlank()
            || hrefValue == "#"
            || hrefValue.lowercase().startsWith("javascript:")) {
            holder.registerProblem(element, MESSAGE, ReplaceHrefQuickFix())
        }
    }
}

/**
 * QuickFix: replaces invalid href with "/" placeholder
 */
class ReplaceHrefQuickFix : LocalQuickFix {
    override fun getName(): String = "Replace invalid href with \"/\""
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        if (element is XmlTag && element.isValid) {
            element.setAttribute("href", "/")
        }
    }
}
