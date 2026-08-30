package com.github.artemkacreate.optimusplugin.startup

import com.github.artemkacreate.optimusplugin.options.RuleSettingsState
import com.github.artemkacreate.optimusplugin.services.OptimusConfigFileService
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class MyProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        // Load JSON config file if configured
        RuleSettingsState.getInstance().syncToRegistry()
        OptimusConfigFileService.getInstance().reloadFromConfigFile()
        thisLogger().info("Optimus Accessibility plugin initialized for project: ${project.name}")
    }
}
