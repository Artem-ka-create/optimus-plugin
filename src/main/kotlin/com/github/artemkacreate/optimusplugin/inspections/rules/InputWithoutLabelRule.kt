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
 * Rule: <input> without <label> or aria-label
 */
class InputWithoutLabelRule : AccessibilityRule {

    override val id = "inputWithoutLabel"
    override val displayName = "Input Without Label"
    override val supportedExtensions = setOf(FileExtension.JS, FileExtension.JSX, FileExtension.TS, FileExtension.TSX)

    companion object {
        private val INPUT_PATTERN = Regex("""<input\b[^>]*?/?>""", RegexOption.IGNORE_CASE)
        private val LABEL_PATTERN = Regex("""\b(aria-label|aria-labelledby|id)\s*=""", RegexOption.IGNORE_CASE)
        private val TYPE_HIDDEN = Regex("""type\s*=\s*["']hidden["']""", RegexOption.IGNORE_CASE)
    }

    override fun analyze(text: String): List<AccessibilityProblem> {
        val problems = mutableListOf<AccessibilityProblem>()
        for (match in INPUT_PATTERN.findAll(text)) {
            val tagText = match.value

            if (TYPE_HIDDEN.containsMatchIn(tagText)) continue
            if (!LABEL_PATTERN.containsMatchIn(tagText)) {
                val insertPos = match.range.first + findInsertPosition(tagText)
                problems.add(
                    AccessibilityProblem(
                        TextRange(match.range.first, match.range.last + 1),
                        "Accessibility: <input> without associated label (add aria-label or id+<label>)",
                        AddAriaLabelFix(insertPos)
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

class AddAriaLabelFix(private val insertOffset: Int) : IntentionAction {
    override fun getText(): String = "Add aria-label=\"\" attribute"
    override fun getFamilyName(): String = "Accessibility fixes"
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        editor ?: return
        editor.document.insertString(insertOffset, " aria-label=\"\"")
    }
    override fun startInWriteAction(): Boolean = true
}


