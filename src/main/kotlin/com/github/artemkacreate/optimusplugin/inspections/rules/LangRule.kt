package com.github.artemkacreate.optimusplugin.inspections.rules

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.util.AttributeResolver
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag

class LangRule : AccessibilityRule {

    override val id: String = "langValid"
    override val displayName: String = "lang attribute must be a valid BCP 47 language tag"

    companion object {
        val BCP47_REGEX = "^[a-z]{2,3}(-[A-Z]{2}|-[0-9]{3})?$".toRegex()
        private const val MESSAGE = "Accessibility: lang attribute must be a valid BCP 47 language tag"
    }

    override fun checkElementByRule(element: PsiElement, file: PsiFile, holder: ProblemsHolder) {
        if (element !is XmlTag) return

        val langAttribute = element.attributes.find { AttributeResolver.normalizeAttrName(it.name) == "lang" } ?: return
        val langValue = AttributeResolver.resolveAttributeValue(langAttribute) ?: return

        if (!isValidLangTag(langValue)) {
            holder.registerProblem(langAttribute, MESSAGE)
        }
    }

    fun isValidLangTag(langValue: String): Boolean {
        val trimmed = langValue.trim()

        if (!BCP47_REGEX.matches(trimmed)) {
            return false
        }

        val commonMistakes = setOf("en_US", "en_GB", "ukr", "rus", "ua", "english")
        return !commonMistakes.contains(trimmed)
    }
}
