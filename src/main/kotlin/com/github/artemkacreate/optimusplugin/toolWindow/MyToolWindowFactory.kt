package com.github.artemkacreate.optimusplugin.toolWindow

import com.github.artemkacreate.optimusplugin.MyBundle
import com.github.artemkacreate.optimusplugin.services.RuleRegistryService
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JCheckBox

class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(project)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(private val project: Project) {
        fun getContent() = JBPanel<JBPanel<*>>().apply {
            layout = BorderLayout(0, 8)
            border = BorderFactory.createEmptyBorder(8, 16, 8, 8)

            val titleLabel = JBLabel(MyBundle["rules.title"])
            val registry = RuleRegistryService.getInstance()

            val checkboxContainer = JBPanel<JBPanel<*>>().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)

                for (rule in registry.getAllRules()) {
                    val checkbox = JCheckBox(rule.displayName, registry.isEnabled(rule.id))
                    checkbox.alignmentX = 0f
                    checkbox.addActionListener {
                        registry.setEnabled(rule.id, checkbox.isSelected)
                        // Перезапускаємо аналіз відкритих файлів
                        restartAnalysis()
                    }
                    add(checkbox)
                }
            }

            add(titleLabel, BorderLayout.NORTH)
            add(checkboxContainer, BorderLayout.CENTER)
        }

        private fun restartAnalysis() {
            // Notify daemon that highlighting settings changed to trigger safe re-highlighting.
            DaemonCodeAnalyzerEx.getInstanceEx(project).settingsChanged()
        }
    }
}
