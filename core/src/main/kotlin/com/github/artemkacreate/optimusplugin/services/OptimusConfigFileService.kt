package com.github.artemkacreate.optimusplugin.services

import com.github.artemkacreate.optimusplugin.inspections.enums.RuleSettingsMode
import com.github.artemkacreate.optimusplugin.options.RuleSettingsState
import com.google.gson.JsonParser
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.ProjectManager
import java.io.File

/**
 * Service that reads an optional JSON configuration file (.optimus.json)
 * and applies rule enable/disable settings from it.
 *
 * JSON format:
 * {
 *   "enabled": true,
 *   "rules": {
 *     "missing-alt": true,
 *     "no-autofocus": false
 *   }
 * }
 *
 * The JSON config takes priority over the UI checkboxes when present.
 */
@Service(Service.Level.APP)
class OptimusConfigFileService {

    companion object {
        private val LOG = Logger.getInstance(OptimusConfigFileService::class.java)

        fun getInstance(): OptimusConfigFileService =
            ApplicationManager.getApplication().getService(OptimusConfigFileService::class.java)
    }

    /**
     * Reload configuration from the JSON file specified in settings.
     * Called after settings are applied or on project open.
     */
    fun reloadFromConfigFile() {
        val settings = RuleSettingsState.getInstance()
        if (settings.state.configMode != RuleSettingsMode.CONFIG_FILE) return

        val configPath = settings.state.configFilePath

        if (configPath.isBlank()) return

        var file = File(configPath)
        if (!file.exists() || !file.isFile) {
            val resolvedFile = ProjectManager.getInstance().openProjects.firstNotNullOfOrNull { project ->
                project.basePath?.let { basePath ->
                    val candidate = File(basePath, configPath)
                    if (candidate.exists() && candidate.isFile) candidate else null
                }
            }
            if (resolvedFile != null) {
                file = resolvedFile
            } else {
                LOG.warn("Optimus config file not found: $configPath")
                return
            }
        }

        try {
            val content = file.readText()
            val config = parseConfig(content)
            applyConfig(config, settings)
            LOG.info("Optimus config loaded from: ${file.absolutePath}")
        } catch (e: Exception) {
            LOG.error("Failed to parse Optimus config file: $configPath", e)
        }
    }

    /**
     * Parse the JSON config content into an OptimusConfig object using Gson.
     */
    private fun parseConfig(content: String): OptimusConfig {
        val jsonObj = JsonParser.parseString(content).asJsonObject

        val enabled = if (jsonObj.has("enabled")) {
            jsonObj.get("enabled").asBoolean
        } else {
            true
        }

        val rules = mutableMapOf<String, Boolean>()
        if (jsonObj.has("rules")) {
            val rulesObj = jsonObj.getAsJsonObject("rules")
            for ((key, value) in rulesObj.entrySet()) {
                try {
                    rules[key] = value.asBoolean
                } catch (e: Exception) {
                    LOG.warn("Invalid value for rule '$key' in config, expected boolean")
                }
            }
        }

        return OptimusConfig(enabled = enabled, rules = rules)
    }

    /**
     * Apply the parsed config to the settings state.
     */
    private fun applyConfig(config: OptimusConfig, settings: RuleSettingsState) {
        // Apply global linter enabled state
        settings.setLinterEnabled(config.enabled)

        // Apply per-rule settings directly to RuleRegistryService so manual settings in settings.state are preserved
        val registry = RuleRegistryService.getInstance()
        for ((ruleId, enabled) in config.rules) {
            registry.setEnabled(ruleId, enabled)
        }
    }

    /**
     * Internal data class representing the parsed config.
     */
    private data class OptimusConfig(
        val enabled: Boolean = true,
        val rules: Map<String, Boolean> = emptyMap()
    )
}
