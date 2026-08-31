package com.github.artemkacreate.optimusplugin.options

import com.github.artemkacreate.optimusplugin.inspections.base.AccessibilityRule
import com.github.artemkacreate.optimusplugin.inspections.enums.RuleSettingsMode
import com.github.artemkacreate.optimusplugin.services.OptimusConfigFileService
import com.github.artemkacreate.optimusplugin.services.RuleRegistryService
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import kotlinx.serialization.json.*
import kotlinx.serialization.json.put

@State(
    name = "RuleSettingsState", storages = [Storage("OptimusAccessibilitySettings.xml")]
)
@Service(Service.Level.APP)
class RuleSettingsState : PersistentStateComponent<RuleSettingsState.SettingsData> {

    class SettingsData {
        var disabledRuleIds: MutableSet<String> = mutableSetOf()
        var enabledRuleIds: MutableSet<String> = mutableSetOf()
        var isLinterCheckEnabled: Boolean = true
        var configFilePath: String = ""
        var configMode: RuleSettingsMode = RuleSettingsMode.IDE_CONFIG
    }

    companion object {
        fun getInstance(): RuleSettingsState = service()
    }

    private var myState = SettingsData()

    override fun getState(): SettingsData = myState

    override fun loadState(state: SettingsData) {
        myState = state
//        syncToRegistry()
    }

    fun isRuleEnabled(ruleId: String): Boolean {
        if (ruleId in myState.disabledRuleIds) return false
        if (ruleId in myState.enabledRuleIds) return true
        // Fall back to the rule's default
        val rule = RuleRegistryService.getInstance().getAllRules().find { it.id == ruleId }
        return rule?.enabledByDefault ?: true
    }

    fun setRuleEnabled(ruleId: String, enabled: Boolean) {
        if (enabled) {
            myState.disabledRuleIds.remove(ruleId)
            myState.enabledRuleIds.add(ruleId)
        } else {
            myState.enabledRuleIds.remove(ruleId)
            myState.disabledRuleIds.add(ruleId)
        }
        RuleRegistryService.getInstance().setEnabled(ruleId, enabled)
        restartAnalysis()
    }

    /**
     * Sync persisted state into the RuleRegistryService at load time.
     */
    fun syncToRegistry() {
        val registry = RuleRegistryService.getInstance()

        if (myState.configMode == RuleSettingsMode.CONFIG_FILE) {
            // Mode 1: Load rule statuses directly from the JSON file
            OptimusConfigFileService.getInstance().reloadFromConfigFile()
        } else {
            // Mode 2: IDE_CONFIG mode - Apply manual user preferences
            val allRules = registry.getAllRules()
            for (rule in allRules) {
                val isEnabled = getRuleStatusBySettingsState(rule)
                registry.setEnabled(rule.id, isEnabled)
            }
        }
        restartAnalysis()
    }

    fun getRuleStatusBySettingsState(rule: AccessibilityRule): Boolean {
        return when (rule.id) {
            in myState.disabledRuleIds -> false
            in myState.enabledRuleIds -> true
            else -> rule.enabledByDefault // Reset to default if not explicitly customized
        }
    }

    private fun restartAnalysis() {
        for (project in ProjectManager.getInstance().openProjects) {
            if (!project.isDisposed) {
                DaemonCodeAnalyzer.getInstance(project).settingsChanged()
            }
        }
    }

    fun generateConfigurationFileContent(): JsonObject {
        return buildJsonObject {
            put("enabled", myState.isLinterCheckEnabled)
            putJsonObject("rules") {
                val allRules = RuleRegistryService.getInstance().getAllRules()
                for (rule in allRules) {
                    put(rule.id, getRuleStatusBySettingsState(rule))
                }
            }
        }
    }

    fun setLinterEnabled(enabled: Boolean) {
        if (myState.isLinterCheckEnabled != enabled) {
            myState.isLinterCheckEnabled = enabled
            restartAnalysis()
        }
    }

}
