package com.github.artemkacreate.optimusplugin.options

import com.github.artemkacreate.optimusplugin.services.RuleRegistryService
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.ProjectManager

@State(
    name = "RuleSettingsState", storages = [Storage("OptimusAccessibilitySettings.xml")]
)
@Service(Service.Level.APP)
class RuleSettingsState : PersistentStateComponent<RuleSettingsState.SettingsData> {

    class SettingsData {
        var disabledRuleIds: MutableSet<String> = mutableSetOf()
        var isLinterCheckEnabled: Boolean = true
    }

    private var myState = SettingsData()

    override fun getState(): SettingsData = myState

    override fun loadState(state: SettingsData) {
        myState = state
        syncToRegistry()
    }

    fun isRuleEnabled(ruleId: String): Boolean = ruleId !in myState.disabledRuleIds

    fun setRuleEnabled(ruleId: String, enabled: Boolean) {
        if (enabled) {
            myState.disabledRuleIds.remove(ruleId)
        } else {
            myState.disabledRuleIds.add(ruleId)
        }
        RuleRegistryService.getInstance().setEnabled(ruleId, enabled)
        restartAnalysis()
    }

    /**
     * Sync persisted disabled state into the RuleRegistryService at load time.
     */
    private fun syncToRegistry() {
        val registry = RuleRegistryService.getInstance()
        for (ruleId in myState.disabledRuleIds) {
            registry.setEnabled(ruleId, false)
        }
    }

    private fun restartAnalysis() {
        for (project in ProjectManager.getInstance().openProjects) {
            if (!project.isDisposed) {
                DaemonCodeAnalyzer.getInstance(project).settingsChanged()
            }
        }
    }

    fun setLinterEnabled(){
        myState.isLinterCheckEnabled = !myState.isLinterCheckEnabled
        restartAnalysis()
    }

    companion object {
        fun getInstance(): RuleSettingsState = service()
    }
}
