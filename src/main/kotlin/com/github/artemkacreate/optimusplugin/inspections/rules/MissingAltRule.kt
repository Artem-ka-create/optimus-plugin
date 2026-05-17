package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityProblem
import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

/**
 * Rule: <img> without alt attribute
 */
class MissingAltRule : AccessibilityRule {

    override val id = "missingAlt"
    override val displayName = "Missing alt"
    override val supportedExtensions = setOf(FileExtension.JS, FileExtension.JSX, FileExtension.TS, FileExtension.TSX)

    companion object {
        private val IMG_PATTERN = Regex("""<\s*img\b[^>]*?/?>""", RegexOption.IGNORE_CASE)
        private val ALT_PATTERN = Regex("""\balt\s*=""", RegexOption.IGNORE_CASE)
    }

    override fun analyze(text: String): List<AccessibilityProblem> {
        val problems = mutableListOf<AccessibilityProblem>()
        for (match in IMG_PATTERN.findAll(text)) {
            if (!ALT_PATTERN.containsMatchIn(match.value)) {
                val insertPos = match.range.first + findInsertPosition(match.value)
                problems.add(
                    AccessibilityProblem(
                        TextRange(match.range.first, match.range.last + 1),
                        "Accessibility: <img> without 'alt' attribute",
                        AddAltAttributeFix(insertPos)
                    )
                )
            }
        }
        return problems
    }

    private fun findInsertPosition(tagText: String): Int {
        val selfClose = tagText.lastIndexOf("/>")
        if (selfClose >= 0) return selfClose
        val close = tagText.lastIndexOf(">")
        if (close >= 0) return close
        return tagText.length
    }
}

class AddAltAttributeFix(private val insertOffset: Int) : IntentionAction {
    override fun getText(): String = "Add alt=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        editor ?: return
        editor.document.insertString(insertOffset, " alt=\"\"")
    }
    override fun startInWriteAction(): Boolean = true
}


