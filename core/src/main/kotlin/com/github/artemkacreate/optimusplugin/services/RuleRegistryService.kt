package com.github.artemkacreate.optimusplugin.services

import com.github.artemkacreate.optimusplugin.inspections.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.FileExtension
import com.github.artemkacreate.optimusplugin.inspections.rules.anchor.AnchorAmbigousTextRule
import com.github.artemkacreate.optimusplugin.inspections.rules.anchor.AnchorHasContentRule
import com.github.artemkacreate.optimusplugin.inspections.rules.anchor.AnchorIsValidRule
import com.github.artemkacreate.optimusplugin.inspections.rules.aria.AriaActiveDescendantRule
import com.github.artemkacreate.optimusplugin.inspections.rules.ControlHasAssociatedLabelRule
import com.github.artemkacreate.optimusplugin.inspections.rules.aria.AriaPropsRule
import com.github.artemkacreate.optimusplugin.inspections.rules.aria.AriaRoleRule
import com.github.artemkacreate.optimusplugin.inspections.rules.HeadingHasContentRule
import com.github.artemkacreate.optimusplugin.inspections.rules.HtmlHasLangRule
import com.github.artemkacreate.optimusplugin.inspections.rules.IframeHasTitleRule
import com.github.artemkacreate.optimusplugin.inspections.rules.ImgRedundantAltRule
import com.github.artemkacreate.optimusplugin.inspections.rules.InputWithoutLabelRule
import com.github.artemkacreate.optimusplugin.inspections.rules.LabelHasForRule
import com.github.artemkacreate.optimusplugin.inspections.rules.LangRule
import com.github.artemkacreate.optimusplugin.inspections.rules.MissingAltRule
import com.github.artemkacreate.optimusplugin.inspections.rules.NoAccessKeyRule
import com.github.artemkacreate.optimusplugin.inspections.rules.aria.NoAriaHiddenOnFocusableRule
import com.github.artemkacreate.optimusplugin.inspections.rules.NoAutofocusRule
import com.github.artemkacreate.optimusplugin.inspections.rules.NoDistractingElementsRule
import com.github.artemkacreate.optimusplugin.inspections.rules.role.NoNonIntElementToIntRoleRule
import com.github.artemkacreate.optimusplugin.inspections.rules.role.NoRedudantRolesRule
import com.github.artemkacreate.optimusplugin.inspections.rules.role.NonIntElementToNonIntRoleRule
import com.github.artemkacreate.optimusplugin.inspections.rules.role.PreferTagOverRoleRule
import com.github.artemkacreate.optimusplugin.inspections.rules.role.RoleHasRequiredAriaPropsRule
import com.github.artemkacreate.optimusplugin.inspections.rules.role.RoleSupportsAriaPropsRule
import com.github.artemkacreate.optimusplugin.inspections.rules.ScopeRule
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
        register(AriaRoleRule())
        register(NoAriaHiddenOnFocusableRule())
        register(LangRule())
        register(LabelHasForRule())
        register(ControlHasAssociatedLabelRule())
        register(AriaActiveDescendantRule())
        register(AnchorAmbigousTextRule())
        register(NoDistractingElementsRule())
        register(NonIntElementToNonIntRoleRule())
        register(NoNonIntElementToIntRoleRule())
        register(NoRedudantRolesRule())
        register(PreferTagOverRoleRule())
        register(RoleHasRequiredAriaPropsRule())
        register(RoleSupportsAriaPropsRule())
        register(ScopeRule())
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
