package com.github.artemkacreate.optimusplugin.inspections.base

import com.intellij.codeInspection.LocalQuickFix

interface  AccessibilityQuickFix : LocalQuickFix{
    override fun getFamilyName(): String = "Accessibility fixes"
}
