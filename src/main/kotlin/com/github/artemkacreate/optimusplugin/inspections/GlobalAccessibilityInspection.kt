package com.github.artemkacreate.optimusplugin.inspections

import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.services.RuleRegistryService
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor

/**
 * Single optimized inspection for accessibility (a11y) rules.
 * Performs ONE tree traversal and delegates each element to all active rules.
 */
class GlobalAccessibilityInspection : LocalInspectionTool() {

    private val ruleRegistry get() = RuleRegistryService.getInstance()

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val ext = holder.file.virtualFile?.extension?.lowercase() ?: return PsiElementVisitor.EMPTY_VISITOR
        val fileExtension = FileExtension.fromExtension(ext) ?: return PsiElementVisitor.EMPTY_VISITOR

        val rules = ruleRegistry.getEnabledRulesForExtension(fileExtension)
        if (rules.isEmpty()) return PsiElementVisitor.EMPTY_VISITOR

        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                val injectionManager = InjectedLanguageManager.getInstance(file.project)

                // Single traversal of the entire PSI tree
                file.accept(object : PsiRecursiveElementWalkingVisitor() {
                    override fun visitElement(element: PsiElement) {
                        // Check element against all rules
                        for (rule in rules) {
                            rule.checkElementByRule(element, file, holder)
                        }

                        // Check injected fragments (HTML in JS/TS template literals)
                        val elementName = element.javaClass.simpleName
                        if (elementName.contains("Literal", ignoreCase = true) ||
                            elementName.contains("String", ignoreCase = true)
                        ) {
                            injectionManager.enumerate(element) { injectedFile, _ ->
                                injectedFile.accept(object : PsiRecursiveElementWalkingVisitor() {
                                    override fun visitElement(injectedElement: PsiElement) {
                                        for (rule in rules) {
                                            rule.checkElementByRule(injectedElement, file, holder)
                                        }
                                        super.visitElement(injectedElement)
                                    }
                                })
                            }
                        }

                        super.visitElement(element)
                    }
                })
            }
        }
    }
}
