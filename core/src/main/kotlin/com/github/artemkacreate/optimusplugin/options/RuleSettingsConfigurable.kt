package com.github.artemkacreate.optimusplugin.options

import com.github.artemkacreate.optimusplugin.inspections.enums.RuleCategory
import com.github.artemkacreate.optimusplugin.services.RuleRegistryService
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.selected
import javax.swing.JComponent


class RuleSettingsConfigurable : Configurable {
    override fun getDisplayName() = "Optimus Accessibility"

    private val settings = RuleSettingsState.getInstance()

    private lateinit var myPanel: DialogPanel

    override fun createComponent(): JComponent {
        val rulesByCategory = RuleRegistryService.getInstance().getAllRules().groupBy { it.category }

        myPanel = panel {
            lateinit var linterCheckboxCell: com.intellij.ui.dsl.builder.Cell<com.intellij.ui.components.JBCheckBox>

            row {
                comment("Configure which accessibility rules are active for your project")
            }

            row {
                linterCheckboxCell =
                    checkBox("Enable accessibility linter").bindSelected(
                        getter = { settings.state.isLinterCheckEnabled },
                        setter = { settings.setLinterEnabled() }
                    )
            }

            separator()

            for (category in RuleCategory.entries) {
                val rules = rulesByCategory[category] ?: continue
                collapsibleGroup(category.displayName) {
                    for (rule in rules) {
                        row {
                            checkBox(rule.displayName).bindSelected(
                                getter = { settings.isRuleEnabled(rule.id) },
                                setter = { enabled -> settings.setRuleEnabled(rule.id, enabled) }
                            )
                            contextHelp("No description available for this rule.")
                        }
                    }
                }.apply {
                    expanded = true
                    enabledIf(linterCheckboxCell.selected)
                }
            }
        }
        return myPanel
    }

    override fun isModified(): Boolean = myPanel.isModified()

    override fun apply() {
        myPanel.apply()
    }

    override fun reset() {
        myPanel.reset()
    }
}
