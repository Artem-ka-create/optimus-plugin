package com.github.artemkacreate.optimusplugin.inspections

import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.services.RuleRegistryService
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiFile

/**
 * Global annotator — Collects problems from all rules.
 */
data class AnnotatorInput(val text: String, val extension: FileExtension)

class AccessibilityAnnotator : ExternalAnnotator<AnnotatorInput, List<AccessibilityProblem>>() {

    private val ruleRegistry get() = RuleRegistryService.getInstance()

    // Step 1: collect data from file (EDT)
    override fun collectInformation(file: PsiFile): AnnotatorInput? {
        val ext = file.virtualFile?.extension?.lowercase() ?: return null
        val fileExtension = FileExtension.fromExtension(ext) ?: return null
        if (ruleRegistry.getEnabledRulesForExtension(fileExtension).isEmpty()) return null
        return AnnotatorInput(file.text, fileExtension)
    }

    // Step 2: rules analysis
    override fun doAnnotate(input: AnnotatorInput): List<AccessibilityProblem> {
        val rules = ruleRegistry.getEnabledRulesForExtension(input.extension)
        return rules.flatMap { it.analyze(input.text) }
    }

    // Step 3: activate Annotations - warnings (EDT)
    override fun apply(file: PsiFile, problems: List<AccessibilityProblem>, holder: AnnotationHolder) {
        for (problem in problems) {
            val builder = holder.newAnnotation(HighlightSeverity.WARNING, problem.message)
                .range(problem.range)
            if (problem.fix != null) {
                builder.withFix(problem.fix)
            }
            builder.create()
        }
    }
}


