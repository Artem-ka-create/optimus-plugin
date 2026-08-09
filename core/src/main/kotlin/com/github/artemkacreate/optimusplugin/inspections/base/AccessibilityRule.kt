package com.github.artemkacreate.optimusplugin.inspections.base

import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.enums.RuleCategory
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Base interface for accessibility rules.
 */
interface AccessibilityRule {
    val id: String

    val displayName: String

    val category: RuleCategory
        get() = RuleCategory.GENERAL

    val supportedExtensions: Set<FileExtension>
        get() = setOf(
            FileExtension.HTML, FileExtension.JS, FileExtension.JSX, FileExtension.TS, FileExtension.TSX, FileExtension.VUE
        )

    /**
     * Check a single PSI element and register problems if found.
     * Called once per element during a single tree traversal.
     */
    fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder)
}
