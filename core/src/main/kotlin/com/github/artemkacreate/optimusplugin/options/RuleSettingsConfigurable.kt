package com.github.artemkacreate.optimusplugin.options

import com.github.artemkacreate.optimusplugin.inspections.enums.RuleCategory
import com.github.artemkacreate.optimusplugin.inspections.enums.RuleSettingsMode
import com.github.artemkacreate.optimusplugin.services.RuleRegistryService
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.selected
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.swing.JComponent


class RuleSettingsConfigurable : Configurable {
    override fun getDisplayName() = "Optimus Accessibility"

    private val settings = RuleSettingsState.getInstance()

    val configurationModes: List<String> = RuleSettingsMode.entries.map { it.configurationModeValue }.toList()

    private lateinit var myPanel: DialogPanel

    private var isLinterEnabled = settings.state.isLinterCheckEnabled
    private var currentMode = settings.state.configMode

    private val manualCheckboxesEnabled = AtomicBooleanProperty(
        isLinterEnabled && currentMode == RuleSettingsMode.IDE_CONFIG
    )
    private val configFileSectionEnabled = AtomicBooleanProperty(
        isLinterEnabled && currentMode == RuleSettingsMode.CONFIG_FILE
    )

    override fun createComponent(): JComponent {
        val rulesByCategory = RuleRegistryService.getInstance().getAllRules().groupBy { it.category }

        myPanel = panel {
            lateinit var linterCheckboxCell: Cell<JBCheckBox>

            row {
                comment("Configure which accessibility rules are active for your project")
            }

            row {
                linterCheckboxCell = checkBox("Enable accessibility linter").bindSelected(
                    getter = { settings.state.isLinterCheckEnabled },
                    setter = { enabled -> settings.setLinterEnabled(enabled) }).applyToComponent {
                    addItemListener {
                        isLinterEnabled = isSelected
                        updateControlEnablements()
                    }
                }
            }
            row("Configuration mode") {
                comboBox(configurationModes).bindItem(
                    getter = { settings.state.configMode.configurationModeValue },
                    setter = { value ->
                        if (value != null) {
                            settings.state.configMode =
                                RuleSettingsMode.entries.find { it.configurationModeValue == value }
                                    ?: RuleSettingsMode.IDE_CONFIG
                        }
                    }).applyToComponent {
                    addActionListener {
                        val selectedStr = selectedItem as? String
                        currentMode = RuleSettingsMode.entries.find { it.configurationModeValue == selectedStr }
                            ?: RuleSettingsMode.IDE_CONFIG
                        updateControlEnablements()
                    }
                }
            }.enabledIf(linterCheckboxCell.selected)

            separator()

            group("Configuration File") {
                row {
                    comment("Optionally load rule settings from a JSON config file (e.g. .optimus.json)")
                }
                row("Config path:") {
                    val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("json")
                        .withTitle("Select Optimus Config File")
                    textFieldWithBrowseButton(fileChooserDescriptor = descriptor).bindText(
                        getter = { settings.state.configFilePath },
                        setter = { path -> settings.state.configFilePath = path }).align(AlignX.FILL)
                }
                row {
                    comment("When Configuration File mode is active, rules are managed by the JSON file.")
                }
            }.enabledIf(configFileSectionEnabled)

            separator()

            group("Manual Rules Configuration") {
                row {
                    button("Export Rules") {
                        exportConfig()
                    }
                    comment("Export Rule Configuration to .json config file")
                }
                indent {
                    row { comment("You should apply changes before configuration export.") }
                }
                for (category in RuleCategory.entries) {
                    val rules = rulesByCategory[category] ?: continue
                    collapsibleGroup(category.displayName) {
                        for (rule in rules) {
                            row {
                                checkBox(rule.displayName).bindSelected(
                                    getter = { settings.isRuleEnabled(rule.id) },
                                    setter = { enabled -> settings.setRuleEnabled(rule.id, enabled) })
                            }
                            indent {
                                row { comment(rule.description.trimIndent()) }
                            }
                        }
                    }.apply {
                        expanded = true
                    }
                }
            }.enabledIf(manualCheckboxesEnabled)
        }
        return myPanel
    }

    private fun updateControlEnablements() {
        manualCheckboxesEnabled.set(isLinterEnabled && currentMode == RuleSettingsMode.IDE_CONFIG)
        configFileSectionEnabled.set(isLinterEnabled && currentMode == RuleSettingsMode.CONFIG_FILE)
    }

    override fun isModified(): Boolean = myPanel.isModified()

    override fun apply() {
        myPanel.apply()
        settings.syncToRegistry()
        reset()
    }

    override fun reset() {
        myPanel.reset()
        isLinterEnabled = settings.state.isLinterCheckEnabled
        currentMode = settings.state.configMode
        updateControlEnablements()
    }

    private fun exportConfig() {
        settings.syncToRegistry()
        val project = ProjectManager.getInstance().openProjects.firstOrNull()

        val descriptor = FileSaverDescriptor(
            "Export Rule Configuration", "Choose a location to save your generated JSON config file", "json"
        )

        val baseDir = project?.basePath?.let { LocalFileSystem.getInstance().findFileByPath(it) }

        val fileWrapper = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project)
            .save(baseDir, "optimus-accessibility.json") ?: return

        try {

            val content = Json { prettyPrint = true }.encodeToString(
                JsonObject.serializer(), settings.generateConfigurationFileContent()
            )

            val ioFile = fileWrapper.file
            ioFile.writeText(content)

            VfsUtil.findFileByIoFile(ioFile, true)?.refresh(false, false)

            Messages.showInfoMessage(
                project, "Rule configuration exported successfully to:\n${ioFile.absolutePath}", "Export Successful"
            )
        } catch (ex: Exception) {
            Messages.showErrorDialog(
                project, "Failed to export rule configuration: ${ex.message}", "Export Failed"
            )
        }
    }

}
