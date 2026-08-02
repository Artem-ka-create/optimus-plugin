package com.github.artemkacreate.optimusplugin.inspections.accessibility

import com.intellij.codeInspection.LocalQuickFix

interface  AccessibilityQuickFix : LocalQuickFix{
    override fun getFamilyName(): String = "Accessibility fixes"
}
