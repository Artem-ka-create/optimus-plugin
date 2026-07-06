package com.github.artemkacreate.optimusplugin.inspections

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

data class ImgProblem(val startOffset: Int, val endOffset: Int, val insertOffset: Int)

/**
 * ExternalAnnotator викликається один раз на файл, а не для кожного PSI-елемента.
 * Фази: collectInformation (EDT) -> doAnnotate (background) -> apply (EDT)
 */
class MissingAltAnnotator : ExternalAnnotator<String, List<ImgProblem>>() {

    companion object {
        private val IMG_PATTERN = Regex("""<img\b[^>]*?/?>""", RegexOption.IGNORE_CASE)
        private val ALT_PATTERN = Regex("""\balt\s*=""", RegexOption.IGNORE_CASE)
        private val SUPPORTED_EXTENSIONS = setOf("js", "jsx", "ts", "tsx")
    }

    override fun collectInformation(file: PsiFile): String? {
        val ext = file.virtualFile?.extension?.lowercase() ?: return null
        if (ext !in SUPPORTED_EXTENSIONS) return null
        return file.text
    }

    override fun doAnnotate(text: String): List<ImgProblem> {
        val problems = mutableListOf<ImgProblem>()
        for (match in IMG_PATTERN.findAll(text)) {
            if (!ALT_PATTERN.containsMatchIn(match.value)) {
                val insertPos = match.range.first + findInsertPosition(match.value)
                problems.add(ImgProblem(match.range.first, match.range.last + 1, insertPos))
            }
        }
        return problems
    }

    override fun apply(file: PsiFile, problems: List<ImgProblem>, holder: AnnotationHolder) {
        for (problem in problems) {
            holder.newAnnotation(
                HighlightSeverity.WARNING,
                "Accessibility: <img> without 'alt' attribute"
            )
                .range(TextRange(problem.startOffset, problem.endOffset))
                .withFix(AddAltAttributeFix(problem.insertOffset))
                .create()
        }
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


