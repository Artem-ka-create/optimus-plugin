package com.github.artemkacreate.optimusplugin.inspections

import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.util.TextRange

data class AccessibilityProblem(
    val range: TextRange,
    val message: String,
    val fix: IntentionAction? = null
)

/**
 * Rules accessibility-analysis.
 */
interface AccessibilityRule {
    val id: String

    val displayName: String

    val supportedExtensions: Set<FileExtension>

    fun analyze(text: String): List<AccessibilityProblem>
}

