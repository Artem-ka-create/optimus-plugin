package com.github.artemkacreate.optimusplugin.services

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.rules.AnchorHasContentRule
import com.github.artemkacreate.optimusplugin.inspections.rules.AnchorIsValidRule
import com.github.artemkacreate.optimusplugin.inspections.rules.AriaPropsRule
import com.github.artemkacreate.optimusplugin.inspections.rules.HeadingHasContentRule
import com.github.artemkacreate.optimusplugin.inspections.rules.HtmlHasLangRule
import com.github.artemkacreate.optimusplugin.inspections.rules.IframeHasTitleRule
import com.github.artemkacreate.optimusplugin.inspections.rules.ImgRedundantAltRule
import com.github.artemkacreate.optimusplugin.inspections.rules.InputWithoutLabelRule
import com.github.artemkacreate.optimusplugin.inspections.rules.MissingAltRule
import com.github.artemkacreate.optimusplugin.inspections.rules.NoAccessKeyRule
import com.github.artemkacreate.optimusplugin.inspections.rules.NoAutofocusRule
import com.github.artemkacreate.optimusplugin.inspections.rules.TabIndexNoPositiveRule
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service

/**
 * Application-level Service to collect rules and activation status
 * Checkboxes in ToolWindow are changing state, annotator reads rules from this class.
 */
@Service(Service.Level.APP)
class RuleRegistryService {

    companion object {
        fun getInstance(): RuleRegistryService =
            ApplicationManager.getApplication().getService(RuleRegistryService::class.java)
    }

    private val rules = mutableListOf<AccessibilityRule>()
    private val enabledRules = mutableSetOf<String>()

    init {
        // Rules registration Tier 1
        register(MissingAltRule())
        register(InputWithoutLabelRule())
        register(AnchorHasContentRule())
        register(AnchorIsValidRule())
        register(IframeHasTitleRule())
        register(HtmlHasLangRule())
        register(HeadingHasContentRule())
        register(ImgRedundantAltRule())
        register(NoAutofocusRule())
        register(NoAccessKeyRule())
        register(TabIndexNoPositiveRule())

        // Rules registration Tier 2
        register(AriaPropsRule())
    }

    private fun register(rule: AccessibilityRule) {
        rules.add(rule)
        enabledRules.add(rule.id)
    }

    fun getAllRules(): List<AccessibilityRule> = rules.toList()

    fun isEnabled(ruleId: String): Boolean = ruleId in enabledRules

    fun setEnabled(ruleId: String, enabled: Boolean) {
        if (enabled) enabledRules.add(ruleId) else enabledRules.remove(ruleId)
    }

    fun getEnabledRulesForExtension(ext: FileExtension): List<AccessibilityRule> {
        return rules.filter { it.id in enabledRules && ext in it.supportedExtensions }
    }
}
